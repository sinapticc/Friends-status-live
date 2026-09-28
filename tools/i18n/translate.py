#!/usr/bin/env python3
"""Make Kotlin files bilingual by wrapping Persian UI literals in t("fa","en") via DeepSeek (AMD API).
Usage: translate.py <file.kt>...   Backups -> logs/oc/T12/backup/, log -> logs/oc/T12/translate.log"""
import sys, re, json, os, shutil, urllib.request, concurrent.futures as cf
ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
KEY = re.search(r'rc-[A-Za-z0-9]+', open(os.path.expanduser('~/.config/opencode/opencode.jsonc')).read()).group(0)
RULES = """You edit one Kotlin source file of an Android app (Jetpack Compose) that is becoming bilingual.
A helper exists: `fun t(fa: String, en: String): String` in package `com.sinapticc.friendsstatus.model` (returns Persian or English by phone language).
TASK: wrap EVERY user-visible Persian string literal as t("<Persian exactly unchanged>", "<English>").
- String templates keep their variables in both: "${n} نفر" -> t("${n} نفر", "${n} people").
- English style: short, casual, friendly, like texting a friend; sentence case; no marketing words.
- The self label «تو» used as a NAME for the user (tab label, "(تو)", "تو · ...", my own card/cell) becomes t("من", "Me"). «تو» as a normal word inside a sentence stays and is translated normally.
- Do NOT wrap: Persian text that is already inside t(...), log messages, map keys/ids compared in code, regexes, or Persian digit tables.
- If a Persian literal is used as a default argument or const val, change the declaration so it still compiles (e.g. make a `val x get() = t(...)` property or compute at call time). No `const val` with t().
- In object/class property initializers that hold labels (e.g. status catalogs), make sure the text is evaluated at use time: use a `get()` property or turn the constructor param into a lambda-free computed value, so the language chosen at startup applies. Keep all public names/signatures used by other files unchanged.
- If this file is not in package com.sinapticc.friendsstatus.model and uses t(, add `import com.sinapticc.friendsstatus.model.t` to the imports (once).
- Change NOTHING else: no refactors, no reformatting, keep comments.
Return ONLY the complete new file content, no explanations, no markdown fences."""
EXTRA = {
 "AppRoot.kt": "Also: the layout direction is currently forced to Rtl; make it `if (com.sinapticc.friendsstatus.model.L10n.isFa) LayoutDirection.Rtl else LayoutDirection.Ltr`.",
 "Catalog.kt": "English status labels to use: toilet On the toilet; sleeping Sleeping; eating Eating; gym At the gym; studying Studying; work At work; driving Driving; gaming Gaming; partying Partying; showering In the shower; coffee Coffee time; sick Sick; walking Out for a walk; dnd Do not disturb; bored Bored; free Free, down for anything; custom Custom; period On my period; spicy Feeling hot; spicy2 On fire; spicy3 Help!; inlove In love; heartbroken Heartbroken; hungover Hungover; angry Angry; crying Crying; date On a date; shopping Shopping; movie Watching a movie; traveling Traveling; cooking Cooking; meditating Meditating; hookah Hookah; cleaning Cleaning; traffic Stuck in traffic; lowbattery Low battery; overthinking Overthinking; maincharacter Main character; busy Busy; period2 Chocolate mode; period3 Don't talk to me; football Watching football; barber At the barber; beard Trimming my beard. StatusDef.label must become language-aware at read time (e.g. store fa and en, and `val label get() = t(fa, en)`), keeping `label` as the property name.",
}
def call(prompt):
    body = json.dumps({"model": "DeepSeek-V4-Flash", "messages": [{"role": "user", "content": prompt}], "max_tokens": 16000, "temperature": 0.1}).encode()
    req = urllib.request.Request("https://developer.amd.com.cn/radeon/api/v1/chat/completions", body, {"Authorization": "Bearer " + KEY, "Content-Type": "application/json"})
    with urllib.request.urlopen(req, timeout=900) as r: d = json.load(r)
    return d["choices"][0]["message"]["content"], d["choices"][0].get("finish_reason")
def frag(src, name, first):
    note = ("\nThis is PART 1 of the file (it starts with package/imports; add the t import there if needed)." if first else
            "\nThis is a LATER FRAGMENT of the file (no package/imports; do NOT add any). Return only the edited fragment.")
    for attempt in range(3):
        try:
            out, fin = call(RULES + note + "\n\nFILE FRAGMENT " + name + ":\n" + src)
            out = re.sub(r'^```[a-zA-Z]*\n|\n```\s*$', '', out.strip()) + "\n"
            if fin == "stop": return out
        except Exception as e: pass
    return None
def split_one(path):
    src = open(path).read(); name = os.path.basename(path); lines = src.split("\n")
    if os.path.exists(os.path.join(ROOT, "logs/oc/T12/backup", name)): return f"DONE {name} (already translated)"
    idx = [i for i, l in enumerate(lines) if l.startswith("@Composable") or l.startswith("    fun ") or l.startswith("    private fun ")]
    parts, cuts = [], [0] + [min(idx, key=lambda i: abs(i - len(lines) * k / 3)) for k in (1, 2)] + [len(lines)]
    cuts = sorted(set(cuts))
    for a, b in zip(cuts, cuts[1:]):
        out = frag("\n".join(lines[a:b]), name, a == 0)
        if out is None: return f"ERR  {name} part {a}-{b}"
        parts.append(out.rstrip("\n"))
    new = "\n".join(parts) + "\n"
    if not (new.startswith("package ") and 0.85 * len(lines) <= new.count("\n") <= 1.3 * len(lines) + 10):
        return f"SKIP {name} split lines {len(lines)}->{new.count(chr(10))}"
    shutil.copy(path, os.path.join(ROOT, "logs/oc/T12/backup", name)); open(path, "w").write(new)
    return f"OK   {name} (split in {len(parts)}) lines {len(lines)}->{new.count(chr(10))}"
def one(path):
    src = open(path).read(); name = os.path.basename(path)
    if os.path.exists(os.path.join(ROOT, "logs/oc/T12/backup", name)): return f"DONE {name} (already translated)"
    for attempt in range(3):
        try:
            out, fin = call(RULES + ("\n" + EXTRA[name] if name in EXTRA else "") + "\n\nFILE " + name + ":\n" + src); break
        except Exception as e:
            err = str(e)
    else:
        return f"ERR  {name} {err}"
    if False: out, fin = call(RULES + ("\n" + EXTRA[name] if name in EXTRA else "") + "\n\nFILE " + name + ":\n" + src)
    out = re.sub(r'^```[a-zA-Z]*\n|\n```\s*$', '', out.strip()) + "\n"
    a, b = src.count("\n"), out.count("\n")
    ok = fin == "stop" and out.startswith("package ") and 0.85 * a <= b <= 1.3 * a + 10
    if ok:
        shutil.copy(path, os.path.join(ROOT, "logs/oc/T12/backup", name)); open(path, "w").write(out)
    return f"{'OK  ' if ok else 'SKIP'} {name} lines {a}->{b} finish={fin}"
fn = split_one if "--split" in sys.argv else one
with cf.ThreadPoolExecutor(4) as ex:
    for line in ex.map(fn, [a for a in sys.argv[1:] if a != "--split"]):
        print(line, flush=True); open(os.path.join(ROOT, "logs/oc/T12/translate.log"), "a").write(line + "\n")
