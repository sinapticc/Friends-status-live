(function go(){const F=window.FSL, INK='#1c1330'; if(!F||!['toilet','gym','sleeping','spicy3','beard','busy','heartbroken','meditating'].every(k=>F.reg[k])) return setTimeout(go,15); if(F._vdone) return; F._vdone=1;
const H={gym:[220,320,40],studying:[230,310,50],driving:[50,150,-120],gaming:[0,60,-80],coffee:[200,320,30],walking:[240,0,90],bored:[60,170,-110],free:[120,230,-40],shopping:[230,0,90],traveling:[40,140,-130],meditating:[0,60,160],hookah:[60,170,-110],overthinking:[160,280,0],maincharacter:[-80,0,110],hungover:[100,220,0],custom:[0,0,0]};
Object.assign(F.hue,H);
Object.assign(F.anch,{eating:[50,18,1],studying:[47,17,.9,-6],work:[49,15,.9],driving:[50,21,.9],gaming:[50,29,.85],partying:[50,21,.95],coffee:[46,27,.85],sick:[50,7,.8],walking:[60,34,.7,-12],dnd:[50,11,.95],bored:[50,33,.95],free:[50,29,.95],custom:[46,21,.9],angry:[50,31,1],crying:[50,29,.9],date:[44,25,.95],traveling:[50,25,.8],hookah:[44,37,.95],traffic:[50,21,.9],lowbattery:[50,9,.8],busy:[44,25,.9],inlove:[50,25,1],heartbroken:[30,24,.8,-15]});
function cap(X,x,y,s,r=0){return `<g transform="rotate(${r} ${x} ${y})">`+X.clay('path',`d="M${x-19*s} ${y+1*s} Q${x-19*s} ${y-19*s} ${x} ${y-19*s} Q${x+19*s} ${y-19*s} ${x+19*s} ${y+1*s} Z"`,'#5b6fd6','#141a4a')+X.clay('path',`d="M${x+6*s} ${y-2*s} H${x+33*s} Q${x+35*s} ${y+4*s} ${x+28*s} ${y+4*s} H${x+4*s} Z"`,'#3a4aa8','#0e1236')+`<circle cx="${x}" cy="${y-19*s}" r="${2.6*s}" fill="#c8f542"/><path d="M${x-8*s} ${y-12*s} Q${x} ${y-15*s} ${x+8*s} ${y-12*s}" stroke="#fff" stroke-width="${1.4*s}" opacity=".35" fill="none"/></g>`;}
function bow(X,x,y,s,r=0){x+=12*s;y-=3*s;return `<g transform="rotate(${r+14} ${x} ${y})">`+X.clay('path',`d="M${x} ${y} C${x-6*s} ${y-12*s} ${x-18*s} ${y-10*s} ${x-16*s} ${y} C${x-18*s} ${y+10*s} ${x-6*s} ${y+12*s} ${x} ${y} Z"`,'#ffc2dc','#e2459a')+X.clay('path',`d="M${x} ${y} C${x+6*s} ${y-12*s} ${x+18*s} ${y-10*s} ${x+16*s} ${y} C${x+18*s} ${y+10*s} ${x+6*s} ${y+12*s} ${x} ${y} Z"`,'#ffc2dc','#e2459a')+X.clay('circle',`cx="${x}" cy="${y}" r="${4.2*s}"`,'#ff8cc4','#c2186b')+`<circle cx="${x-10*s}" cy="${y-3*s}" r="${1.5*s}" fill="#fff" opacity=".8"/></g>`;}
function bucket(X,x,y,s,r=0){return `<g transform="rotate(${r} ${x} ${y})">`+X.clay('ellipse',`cx="${x}" cy="${y}" rx="${26*s}" ry="${6*s}"`,'#fff0a0','#e0a012')+X.clay('path',`d="M${x-16*s} ${y-1*s} L${x-12*s} ${y-18*s} Q${x} ${y-22*s} ${x+12*s} ${y-18*s} L${x+16*s} ${y-1*s} Z"`,'#ffe98a','#e5a816')+`<path d="M${x-15*s} ${y-6*s} Q${x} ${y-9*s} ${x+15*s} ${y-6*s}" stroke="#b77a00" stroke-width="${1.2*s}" stroke-dasharray="${2*s} ${2*s}" fill="none"/></g>`;}
F.headwear={boy:cap,girl:bow,neutral:bucket};
F.hook=function(X){ if(!X.v) return; const e=X.eye, m=X.mouth, b=X.blush;
  if(X.v==='girl'){ X.eye=(x,y,r,o={})=>{ let s=e(x,y,r,o); const k=o.kind||'open'; if(r<3.4||['heart','star','spiral','line','squeeze'].includes(k)) return s; const g=x<50?-1:1, c=o.color||INK, w=r*.24;
      const top=k==='closed'?y+r*.1:k==='happy'?y-r*.2:y-r*(o.lid?(1-o.lid*1.4):.7);
      return s+`<path d="M${x+g*r*.55} ${top} l${g*r*.4} ${-r*.45} M${x+g*r*.85} ${top+r*.25} l${g*r*.5} ${-r*.2} M${x+g*r*.95} ${top+r*.6} l${g*r*.5} ${r*.05}" stroke="${c}" stroke-width="${w}" stroke-linecap="round"/>`; };
    X.blush=(x1,x2,y,r=5,o=.55)=>b(x1,x2,y,r*1.1,Math.min(1,o+.2)); }
  if(X.v==='boy'){ X.mouth=(t,x,y,s)=>{ let out=m(t,x,y,s); if(s<3.4||X.noStubble) return out; const dy=t==='open'?s*1.9:s*1.05;
      return out+[[-1.1,.25],[-.6,.55],[0,.7],[.6,.55],[1.1,.25],[-.85,-.05],[.85,-.05]].map(([a,bb])=>`<circle cx="${x+a*s}" cy="${y+dy+bb*s}" r="${s*.1}" fill="${INK}" opacity=".32"/>`).join(''); }; }
};
F.post=function(X,key){ if(!X.v||X.noHat) return ''; let out='';
  const a=F.anch[key]; if(a) out+=F.headwear[X.v](X,a[0],a[1],a[2],a[3]||0);
  if(/^spicy/.test(key)){ const P={spicy:[41,59,58],spicy2:[40,60,58],spicy3:[38,62,56]}[key];
    if(X.v==='boy') out+=`<path d="M${P[0]-5} ${P[2]-10} L${P[0]+5} ${P[2]-9}" stroke="${INK}" stroke-width="2.6" stroke-linecap="round"/><path d="M${P[1]-5} ${P[2]-11} Q${P[1]} ${P[2]-18} ${P[1]+6} ${P[2]-13}" stroke="${INK}" stroke-width="2.6" stroke-linecap="round" fill="none"/>`;
    if(X.v==='girl'&&key==='spicy') out+=`<path d="M46 71 h5 v2.6 q-2.5 1.4 -5 0 Z" fill="#fff"/><path d="M42 72.5 Q50 76 58 71" stroke="#e8336a" stroke-width="2.2" fill="none" stroke-linecap="round"/>`;
    if(X.v==='neutral') out+=`<g fill="#fff" opacity=".8"><circle cx="16" cy="30" r="5"/><circle cx="12" cy="22" r="3.6"/><circle cx="86" cy="30" r="5"/><circle cx="90" cy="22" r="3.6"/></g>`; }
  return out; };

const T0=F.reg.toilet, G0=F.reg.gym, S0=F.reg.sleeping;
F.reg.toilet=function(X){ if(!X.v||X.v==='boy'){ let s=T0(X); if(X.v) { X.noHat=0; s+=cap(X,45,21,.8,-8); } return s; }
  X.noHat=1;
  const body=X.shadow(50,93,30)+X.clay('rect','x="60" y="26" width="26" height="36" rx="7"','#ffffff','#aab2cf')+X.clay('path','d="M32 70 H70 L63 90 H40 Z"','#ffffff','#a9b0cc')+X.clay('ellipse','cx="50" cy="70" rx="29" ry="8"','#ffffff','#b7bdd6')+X.flat('ellipse','cx="50" cy="69" rx="21" ry="4.5"','#8b93b3')+
    X.clay('ellipse','cx="46" cy="60" rx="25" ry="13"','#c58a5c','#5b331c')+X.clay('ellipse','cx="46" cy="45" rx="19" ry="11"','#cf9667','#6a3c22')+X.clay('path','d="M34 38 C35 26 48 22 53 13 C57 22 62 30 58 38 Z"','#d9a274','#6e3e22')+X.gloss(38,40,6,3)+X.gloss(34,55,6,2.5,-15,.5);
  if(X.v==='girl') return body+X.eye(38,48,5.2,{look:[.8,-.6]})+X.eye(52,48,5.2,{look:[.8,-.6]})+X.blush(31,59,56,4.5)+
    `<path d="M42 55 Q46 60 51 55 Q48 58 42 55 Z" fill="#e8336a" stroke="#e8336a" stroke-width="2" stroke-linejoin="round"/>`+
    `<path d="M60 56 Q70 44 72 30" stroke="#b37a4e" stroke-width="6" stroke-linecap="round" fill="none"/>`+
    `<g transform="rotate(12 74 24)">`+X.clay('rect','x="66" y="12" width="15" height="24" rx="4"','#ff9cc8','#c2186b')+`<circle cx="70" cy="16" r="1.8" fill="#1c1330"/></g>`+X.spark(86,12,5,'#fff')+bow(X,44,15,.8,0);
  return body+X.eye(38,48,5.2,{look:[.2,.9]})+X.eye(52,48,5.2,{look:[.2,.9]})+X.blush(31,59,56,4.5)+X.mouth('open',45,56,4)+
    `<g transform="rotate(-8 50 70)">`+X.clay('path','d="M28 62 L50 66 L72 62 L72 80 L50 84 L28 80 Z"','#ffffff','#c9cee0')+`<rect x="32" y="66" width="7" height="6" fill="#ff5ca8"/><rect x="41" y="67" width="6" height="10" fill="#4fd1ff"/><rect x="54" y="67" width="7" height="5" fill="#ffd23a"/><rect x="63" y="66" width="6" height="11" fill="#7cf06a"/><path d="M50 66 V84" stroke="#aab1c6" stroke-width="1.2"/></g>`+bucket(X,45,24,.8,-6); };
F.reg.gym=function(X){ if(!X.v||X.v==='boy') return G0(X);
  X.noHat=1; X.noStubble=1;
  const plates=X.clay('rect','x="4" y="44" width="10" height="26" rx="5"','#7a74b8','#231f48')+X.clay('rect','x="86" y="44" width="10" height="26" rx="5"','#7a74b8','#231f48')+X.clay('circle','cx="19" cy="57" r="17"','#8c86cc','#27234f')+X.clay('circle','cx="81" cy="57" r="17"','#8c86cc','#27234f')+X.flat('circle','cx="19" cy="57" r="6"','#1d1a3a')+X.flat('circle','cx="81" cy="57" r="6"','#1d1a3a');
  const bodyR=X.clay('rect','x="27" y="38" width="46" height="38" rx="17"','#ff9a88','#d4304c')+X.flat('rect','x="28" y="44" width="44" height="6"','#fff')+X.gloss(38,56,6,3,-20,.55);
  if(X.v==='girl') return X.shadow(50,92,40)+`<path d="M36 42 Q30 22 48 10 M64 42 Q70 22 52 10" stroke="#e0445c" stroke-width="7" stroke-linecap="round" fill="none"/>`+X.clay('circle','cx="50" cy="9" r="5.5"','#ff9a88','#d93a52')+plates+bodyR+
    X.eye(41,58,4.6,{kind:'closed'})+X.eye(59,58,4.6,{kind:'closed'})+X.mouth('smile',50,65,4)+X.blush(33,67,64,4)+bow(X,54,40,.6,10)+X.spark(10,24,4,'#fff')+X.spark(90,28,3,'#fff');
  return X.shadow(50,92,40)+plates+bodyR+X.eye(41,58,4.8)+X.eye(59,58,4.8)+X.mouth('smile',50,65,4.5)+X.blush(33,67,65,4)+
    `<path d="M68 50 Q80 44 80 34" stroke="#ff9a88" stroke-width="7" stroke-linecap="round" fill="none"/>`+
    X.clay('rect','x="74" y="10" width="14" height="26" rx="5"','#bfeaff','#3fa0e0')+X.flat('rect','x="76" y="6" width="10" height="6" rx="2"','#c8f542')+X.drop(22,26,3); };
F.reg.sleeping=function(X){ if(!X.v) return S0(X); X.noHat=1;
  const cloud=X.shadow(50,86,32,.25)+X.clay('path','d="M22 76 C8 76 8 56 23 55 C21 40 40 34 49 45 C55 32 78 34 77 51 C92 51 94 76 78 76 Z"','#ffffff','#a79ff0')+X.gloss(32,55,7,3.5);
  if(X.v==='boy') return cloud+X.clay('path','d="M18 60 C16 34 34 26 50 28 C66 26 84 34 82 56 C74 44 64 40 50 42 C36 40 26 46 18 60 Z"','#4a5bd0','#141a4a')+
    `<path d="M40 44 L38 60 M60 44 L62 60" stroke="#fff" stroke-width="2" stroke-linecap="round"/><circle cx="38" cy="61" r="2" fill="#fff"/><circle cx="62" cy="61" r="2" fill="#fff"/>`+
    X.eye(42,60,5,{kind:'closed'})+X.eye(58,60,5,{kind:'closed'})+X.mouth('open',50,67,5)+`<circle cx="66" cy="70" r="6" fill="#bfe9ff" opacity=".75"/><circle cx="64" cy="68" r="1.6" fill="#fff"/>`+X.z(72,10,12)+X.z(88,0,8)+X.z(60,0,6,'#b9afff');
  if(X.v==='girl') return cloud+`<path d="M24 56 Q50 48 76 56 L76 64 Q50 58 24 64 Z" fill="#ffb0d0"/>`+X.clay('path','d="M30 54 Q40 50 48 56 Q48 66 38 66 Q30 64 30 54 Z M52 56 Q60 50 70 54 Q70 64 62 66 Q52 66 52 56 Z"','#ffd6ea','#ff7ab6')+
    X.eye(40,58,4.6,{kind:'closed',color:'#c2186b'})+X.eye(60,58,4.6,{kind:'closed',color:'#c2186b'})+X.mouth('smile',50,70,3.6)+X.blush(32,68,70,4)+bow(X,56,38,.8,0)+X.z(80,16,7,'#ffb0d0');
  return X.shadow(50,88,34,.25)+X.clay('path','d="M16 52 Q50 36 84 52 L86 82 Q50 92 14 82 Z"','#ffe98a','#e0a012')+`<path d="M16 62 Q50 50 86 62 M15 72 Q50 62 86 72" stroke="#2fb3a3" stroke-width="4" fill="none"/>`+
    X.clay('circle','cx="50" cy="40" r="20"','#ffffff','#a79ff0')+X.gloss(42,30,5,2.5)+X.eye(43,42,4.2,{kind:'closed'})+X.eye(57,42,4.2,{kind:'closed'})+X.mouth('smile',50,49,3)+X.blush(37,63,48,3)+
    X.clay('path','d="M30 46 Q50 60 70 46 L74 56 Q50 66 26 56 Z"','#fff3b8','#e5b820')+X.z(76,10,8); };
F.cache={};
document.querySelectorAll('fsl-char').forEach(e=>{try{e.render()}catch(_){}});
})();
