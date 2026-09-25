(function go(){const F=window.FSL; if(!F||!F.mix) return setTimeout(go,15); const INK='#1c1330';
F.meOpts={
  tints:['#c8f542','#ff8cc4','#8fb8ff','#ffd23a','#b49bff','#6ff0c8','#ff9a6a','#4a5bd0','#2b2b33'],
  faces:[['happy','Happy'],['smug','Smug'],['wink','Wink'],['shock','Shook'],['sleepy','Sleepy'],['cheeky','Cheeky']],
  accs:[['none','None','all'],['cap','Cap','boy'],['beanie','Beanie','boy'],['chain','Gold chain','boy'],['shades','Shades','boy'],['headphones','Headphones','all'],['bow','Bow','girl'],['clip','Hair clips','girl'],['earrings','Earrings','girl'],['glasses','Round glasses','neutral'],['bucket','Bucket hat','neutral'],['crown','Crown','all']],
  outfits:[['none','None','all'],['hoodie','Hoodie','boy'],['jersey','Jersey','boy'],['cardigan','Cardigan','girl'],['dress','Pinafore','girl'],['tee','Oversized tee','neutral'],['scarf','Scarf','all']]
};
function acc(X,a){
  if(a==='cap') return X.clay('path','d="M24 34 Q24 8 50 8 Q76 8 76 34 Z"','#5b6fd6','#141a4a')+X.clay('path','d="M58 30 H92 Q94 36 86 37 H56 Z"','#3a4aa8','#0e1236')+'<circle cx="50" cy="8" r="3" fill="#c8f542"/>';
  if(a==='beanie') return X.clay('path','d="M22 36 Q22 6 50 6 Q78 6 78 36 Z"','#ff6a5c','#a8122c')+X.clay('rect','x="18" y="30" width="64" height="10" rx="5"','#ff8a7a','#c4122e')+X.clay('circle','cx="50" cy="4" r="6"','#ffffff','#c8cde0')+'<path d="M28 34 V40 M36 33 V40 M44 33 V40 M52 33 V40 M60 33 V40 M68 33 V40 M76 34 V40" stroke="#fff" stroke-width="1" opacity=".3"/>';
  if(a==='bucket') return X.clay('ellipse','cx="50" cy="32" rx="38" ry="8"','#fff0a0','#e0a012')+X.clay('path','d="M26 31 L32 8 Q50 2 68 8 L74 31 Z"','#ffe98a','#e5a816')+'<path d="M28 24 Q50 19 72 24" stroke="#b77a00" stroke-width="1.4" stroke-dasharray="3 3" fill="none"/>';
  if(a==='crown') return X.clay('path','d="M30 30 L32 10 L42 20 L50 6 L58 20 L68 10 L70 30 Z"','#ffe98a','#e0a012')+'<circle cx="50" cy="22" r="2.6" fill="#ff5ca8"/><circle cx="38" cy="25" r="1.8" fill="#4fd1ff"/><circle cx="62" cy="25" r="1.8" fill="#4fd1ff"/>';
  if(a==='bow') return '<g transform="translate(18 -14)">'+X.clay('path','d="M50 30 C44 18 32 20 34 30 C32 40 44 42 50 30 Z"','#ffc2dc','#e2459a')+X.clay('path','d="M50 30 C56 18 68 20 66 30 C68 40 56 42 50 30 Z"','#ffc2dc','#e2459a')+X.clay('circle','cx="50" cy="30" r="4.5"','#ff8cc4','#c2186b')+'</g>';
  if(a==='clip') return '<g><rect x="22" y="26" width="16" height="6" rx="3" fill="#ff8cc4" transform="rotate(-30 30 29)"/><rect x="26" y="34" width="14" height="5" rx="2.5" fill="#6ff0c8" transform="rotate(-30 33 36)"/>'+X.spark(72,24,5,'#ffe98a')+'</g>';
  if(a==='headphones') return '<path d="M20 50 Q20 12 50 12 Q80 12 80 50" stroke="#2b2440" stroke-width="6" fill="none"/>'+X.clay('rect','x="12" y="42" width="14" height="22" rx="7"','#ff5ca8','#8a1050')+X.clay('rect','x="74" y="42" width="14" height="22" rx="7"','#ff5ca8','#8a1050');
  if(a==='glasses') return '<g stroke="#1c1330" stroke-width="2.6" fill="rgba(255,255,255,.2)"><circle cx="38" cy="50" r="10"/><circle cx="62" cy="50" r="10"/></g><path d="M48 49 H52" stroke="#1c1330" stroke-width="2.6"/>'+X.gloss(34,46,3,1.5,-30,.8)+X.gloss(58,46,3,1.5,-30,.8);
  if(a==='shades') return '<g fill="#15101f"><path d="M24 44 H48 Q48 58 36 58 Q24 58 24 44 Z"/><path d="M52 44 H76 Q76 58 64 58 Q52 58 52 44 Z"/></g><path d="M47 46 Q50 43 53 46" stroke="#15101f" stroke-width="2.4" fill="none"/><path d="M28 47 l6 4 M56 47 l6 4" stroke="#fff" stroke-width="2" stroke-linecap="round" opacity=".8"/>';
  if(a==='earrings') return '<circle cx="19" cy="62" r="3.6" fill="#ffe98a" stroke="#e0a012" stroke-width="1"/><circle cx="81" cy="62" r="3.6" fill="#ffe98a" stroke="#e0a012" stroke-width="1"/>';
  if(a==='chain') return '<path d="M30 76 Q50 92 70 76" stroke="#ffd23a" stroke-width="3.4" stroke-dasharray="3.4 1.6" fill="none"/>'+X.clay('circle','cx="50" cy="86" r="5"','#fff3a0','#d99a00');
  return '';
}
function outfit(X,o){
  if(o==='hoodie') return X.clay('path','d="M18 74 Q50 62 82 74 Q84 90 72 94 H28 Q16 90 18 74 Z"','#4a5bd0','#141a4a')+'<path d="M42 70 V82 M58 70 V82" stroke="#fff" stroke-width="2" stroke-linecap="round"/><circle cx="42" cy="83" r="1.8" fill="#fff"/><circle cx="58" cy="83" r="1.8" fill="#fff"/>';
  if(o==='jersey') return X.clay('path','d="M18 74 Q50 64 82 74 Q84 90 72 94 H28 Q16 90 18 74 Z"','#3fcf8a','#0f6a3e')+'<text x="50" y="90" text-anchor="middle" font-family="Arial Black,sans-serif" font-size="14" font-weight="900" fill="#fff">10</text>';
  if(o==='cardigan') return X.clay('path','d="M18 74 Q50 64 82 74 Q84 90 72 94 H28 Q16 90 18 74 Z"','#ffc2dc','#d9588e')+'<path d="M50 68 V94" stroke="#fff" stroke-width="2"/><circle cx="46" cy="78" r="1.6" fill="#fff"/><circle cx="46" cy="86" r="1.6" fill="#fff"/>';
  if(o==='dress') return X.clay('path','d="M28 70 H72 L80 94 H20 Z"','#b49bff','#5a36c8')+'<circle cx="38" cy="82" r="2" fill="#fff" opacity=".7"/><circle cx="56" cy="88" r="2" fill="#fff" opacity=".7"/><circle cx="64" cy="78" r="2" fill="#fff" opacity=".7"/>';
  if(o==='tee') return X.clay('path','d="M14 76 Q50 62 86 76 L84 94 H16 Z"','#fff3d6','#d9c49a')+X.spark(50,84,5,'#ff9a6a');
  if(o==='scarf') return X.clay('path','d="M22 70 Q50 82 78 70 L80 78 Q50 90 20 78 Z"','#ff6a5c','#a8122c')+X.clay('rect','x="62" y="76" width="10" height="18" rx="3"','#ff6a5c','#a8122c');
  return '';
}
F.reg.me=function(X,o){ const t=o.tint||'#c8f542', f=o.face||'happy', a=o.acc||'none', w=o.outfit||'none';
  const hi=F.mix(t,'#ffffff',.45), lo=F.mix(t,'#1c1330',.45);
  let s=X.shadow(50,95,28)+X.clay('path','d="M16 60 Q14 22 50 20 Q86 22 84 60 Q84 94 50 94 Q16 94 16 60 Z"',hi,lo)+outfit(X,w)+X.gloss(34,34,8,4.5);
  const E={happy:()=>X.eyes(38,62,50,6)+X.mouth('smile',50,62,6),smug:()=>X.eyes(38,62,50,6,{lid:.48})+X.mouth('smirk',50,62,6),wink:()=>X.eye(38,50,6)+X.eye(62,50,6,{kind:'happy'})+X.mouth('open',50,61,6),
    shock:()=>X.eyes(38,62,48,7.5)+X.mouth('o',50,65,6),sleepy:()=>X.eyes(38,62,50,6,{kind:'closed'})+X.mouth('smile',50,62,4)+X.z(74,12,7),cheeky:()=>X.eyes(38,62,50,6,{kind:'happy'})+X.mouth('smile',50,60,6)+'<path d="M53 64 q2 7 6 5 q1 -4 -2 -6" fill="#ff6f8e"/>'};
  s+=(E[f]||E.happy)()+X.blush(28,72,60,5);
  return s+acc(X,a);
};
F.meAcc=acc;
Object.keys({cap:1,bow:1,bucket:1,beanie:1,crown:1,headphones:1}).forEach(k=>F.defAcc(k,X=>'<g transform="translate(0 20)">'+acc(X,k)+'</g>'));
F.pending.forEach(el=>{if(el.getAttribute('c')==='me'){F.pending.delete(el);el.render();}});
})();
