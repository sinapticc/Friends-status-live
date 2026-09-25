// Renders the design's SVG characters to WebP for Android.
// Usage: node render.mjs ../../app/src/main/res/drawable-nodpi   (needs `npm i playwright`)
import { chromium } from 'playwright';
import fs from 'fs';
const OUT = process.argv[2]; const PX = 448;
const b = await chromium.launch(); const p = await b.newPage();
await p.goto(new URL('./page.html', import.meta.url).href); await p.waitForTimeout(500);
const jobs = await p.evaluate(()=>{
  const F = FSL, J = [];
  F.list.forEach(({key})=>J.push(['ch_'+key, F.svg(key,{v:'neutral'})]));
  Object.keys(F.accReg).forEach(k=>J.push(['acc_'+k, F.svg('__acc_'+k)]));
  const O = F.meOpts;
  F.reg.__meBody = (X,o)=>{ const t=o.tint, hi=F.mix(t,'#ffffff',.45), lo=F.mix(t,'#1c1330',.45);
    return X.shadow(50,95,28)+X.clay('path','d="M16 60 Q14 22 50 20 Q86 22 84 60 Q84 94 50 94 Q16 94 16 60 Z"',hi,lo)+X.gloss(34,34,8,4.5); };
  const E={happy:X=>X.eyes(38,62,50,6)+X.mouth('smile',50,62,6),smug:X=>X.eyes(38,62,50,6,{lid:.48})+X.mouth('smirk',50,62,6),wink:X=>X.eye(38,50,6)+X.eye(62,50,6,{kind:'happy'})+X.mouth('open',50,61,6),
    shock:X=>X.eyes(38,62,48,7.5)+X.mouth('o',50,65,6),sleepy:X=>X.eyes(38,62,50,6,{kind:'closed'})+X.mouth('smile',50,62,4)+X.z(74,12,7),cheeky:X=>X.eyes(38,62,50,6,{kind:'happy'})+X.mouth('smile',50,60,6)+'<path d="M53 64 q2 7 6 5 q1 -4 -2 -6" fill="#ff6f8e"/>'};
  F.reg.__meFace = (X,o)=>E[o.face](X)+X.blush(28,72,60,5);
  F.reg.__meOutfit = (X,o)=>F.meOutfit(X,o.outfit);
  F.reg.__meAcc = (X,o)=>F.meAcc(X,o.acc);
  O.tints.forEach((t,i)=>J.push(['me_body_'+i, F.svg('__meBody',{tint:t})]));
  O.faces.forEach(([k])=>J.push(['me_face_'+k, F.svg('__meFace',{face:k})]));
  O.outfits.filter(o=>o[0]!=='none').forEach(([k])=>J.push(['me_outfit_'+k, F.svg('__meOutfit',{outfit:k})]));
  O.accs.filter(o=>o[0]!=='none').forEach(([k])=>J.push(['me_acc_'+k, F.svg('__meAcc',{acc:k})]));
  return J;
});
let n=0, bytes=0;
for (const [name, svg] of jobs) {
  const data = await p.evaluate(async ([svg, PX])=>{
    const img = new Image(); img.src = 'data:image/svg+xml;charset=utf-8,'+encodeURIComponent(svg); await img.decode();
    const c = document.createElement('canvas'); c.width = c.height = PX; c.getContext('2d').drawImage(img,0,0,PX,PX);
    return c.toDataURL('image/webp', 0.88);
  }, [svg, PX]);
  const buf = Buffer.from(data.split(',')[1], 'base64'); fs.writeFileSync(`${OUT}/${name}.webp`, buf); n++; bytes+=buf.length;
}
console.log(n, 'images', Math.round(bytes/1024), 'KB');
await b.close();
