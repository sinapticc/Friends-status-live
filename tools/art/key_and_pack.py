#!/usr/bin/env python3
"""Chroma-key a generated image on flat magenta (#FF00FF) to real alpha, trim, pad to square, save PNG master + WebP.
Usage: key_and_pack.py <in.png> <out_master.png> <out.webp> [--keyed]   (--keyed: input already has alpha, only pad/pack)"""
import sys, subprocess, numpy as np
from PIL import Image
src, master, webp = sys.argv[1:4]; keyed = "--keyed" in sys.argv
im = Image.open(src).convert("RGBA"); a = np.asarray(im).astype(np.float32)
if not keyed:
    r, g, b = a[..., 0], a[..., 1], a[..., 2]
    # distance from magenta: magenta has high R, high B, low G
    mag = np.clip((np.minimum(r, b) - g) / 255.0, 0, 1)          # 1 = pure magenta
    alpha = np.clip((0.55 - mag) / 0.35, 0, 1)                    # soft edge
    # despill: pull magenta tint out of semi-transparent edge pixels
    spill = np.maximum(0, np.minimum(r, b) - g) * (1 - alpha)
    a[..., 0] -= spill; a[..., 2] -= spill
    a[..., 3] = alpha * 255
im = Image.fromarray(np.clip(a, 0, 255).astype(np.uint8), "RGBA")
bbox = im.getchannel("A").point(lambda v: 255 if v > 8 else 0).getbbox()
if not bbox: sys.exit("empty image after keying")
im = im.crop(bbox); w, h = im.size; side = int(max(w, h) / 0.84)  # ~8% margin each side
canvas = Image.new("RGBA", (side, side), (0, 0, 0, 0)); canvas.paste(im, ((side - w) // 2, (side - h) // 2))
canvas.resize((1024, 1024), Image.LANCZOS).save(master)
small = canvas.resize((512, 512), Image.LANCZOS); tmp = webp + ".tmp.png"; small.save(tmp)
subprocess.run(["cwebp", "-quiet", "-q", "82", "-alpha_q", "90", tmp, "-o", webp], check=True)
subprocess.run(["rm", tmp]); print("ok", master, webp)
