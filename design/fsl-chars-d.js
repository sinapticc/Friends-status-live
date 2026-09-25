(function go(){if(!window.FSL)return setTimeout(go,15);const D=(k,l,f)=>window.FSL.def(k,l,f);
const flame='M50 6 C60 22 82 32 82 60 C82 82 67 94 50 94 C33 94 18 82 18 60 C18 44 28 36 32 22 C37 32 41 35 46 36 C43 25 45 14 50 6 Z';
const inner='M50 40 C56 50 68 56 68 72 C68 84 60 90 50 90 C40 90 32 84 32 72 C32 62 40 58 42 50 C45 55 47 56 49 56 C47 51 48 45 50 40 Z';
const peach=(X,x,y,s)=>X.clay('circle',`cx="${x}" cy="${y}" r="${s}"`,'#ffc29e','#ff5f6d')+`<path d="M${x} ${y-s*.9} Q${x-s*.35} ${y} ${x} ${y+s*.8}" stroke="#d63e55" stroke-width="${s*.14}" fill="none" stroke-linecap="round" opacity=".7"/>`+`<path d="M${x} ${y-s} q${s*.7} ${-s*.8} ${s*1.1} ${-s*.3} q${-s*.6} ${s*.5} ${-s*1.1} ${s*.3}" fill="#5fcf5a"/>`+X.gloss(x-s*.4,y-s*.35,s*.28,s*.18,-30,.8);
const egg=(X,x,y,s,r=-35)=>`<g transform="rotate(${r} ${x} ${y})">`+X.clay('ellipse',`cx="${x}" cy="${y+s*.3}" rx="${s*.62}" ry="${s*1.15}"`,'#b27bff','#4b1c9e')+`<path d="M${x-s*.6} ${y-s*.55} Q${x} ${y-s*.15} ${x+s*.6} ${y-s*.55} L${x+s*.2} ${y-s*.85} L${x+s*.05} ${y-s*1.3} L${x-s*.1} ${y-s*.85} Z" fill="#58c24a"/>`+X.gloss(x-s*.25,y,s*.15,s*.4,0,.7)+`</g>`;
const fan=(X,x,y,s,r=20)=>`<g transform="rotate(${r} ${x} ${y})"><path d="M${x} ${y} L${x-s} ${y-s*1.3} A${s*1.65} ${s*1.65} 0 0 1 ${x+s} ${y-s*1.3} Z" fill="#fff4f7"/><path d="M${x} ${y} L${x-s*.5} ${y-s*1.55} M${x} ${y} L${x} ${y-s*1.62} M${x} ${y} L${x+s*.5} ${y-s*1.55}" stroke="#ff8cb4" stroke-width="${s*.12}"/><rect x="${x-s*.12}" y="${y-s*.1}" width="${s*.24}" height="${s*.7}" rx="${s*.1}" fill="#b07040"/></g>`;
D('period','Period',X=>
  X.shadow(50,94,30)+
  X.clay('rect','x="42" y="8" width="16" height="12" rx="4"','#ffffff','#b9bfd6')+
  X.clay('path','d="M40 18 H60 Q62 26 70 28 Q84 32 84 50 V76 Q84 90 70 90 H30 Q16 90 16 76 V50 Q16 32 30 28 Q38 26 40 18 Z"','#ff9cb6','#d9345f')+
  `<path d="M24 58 H76 M24 66 H76 M24 74 H76" stroke="#fff" stroke-width="1.6" opacity=".25"/>`+
  X.gloss(28,40,6,3.5)+
  X.eye(38,46,4.6,{kind:'squeeze'})+X.eye(62,46,4.6,{kind:'squeeze',flip:1})+X.brows(38,62,37,4.4,-20)+
  X.mouth('wavy',50,56,5)+X.blush(28,72,53,4.5,.8)+
  `<path d="M24 70 Q36 82 50 72 Q64 82 76 70" stroke="#ffc6d6" stroke-width="7" stroke-linecap="round" fill="none"/>`+
  `<g transform="rotate(-18 84 80)">`+X.clay('rect','x="76" y="68" width="18" height="24" rx="3"','#8a4a28','#3a190b')+`<path d="M76 76 H94 M76 84 H94 M85 68 V92" stroke="#2a1006" stroke-width="1.2" opacity=".6"/>`+X.flat('path','d="M76 80 L94 76 V92 H76 Z"','#c7c9d9')+`</g>`+
  X.heart(16,24,5)+`<path d="M86 22 l3 -4 M92 28 l4 -2" stroke="#ff9cb6" stroke-width="2" stroke-linecap="round"/>`);
D('spicy','Mildly spicy',X=>
  X.shadow(50,95,26)+
  X.clay('path',`d="${flame}" transform="translate(50 60) scale(.84) translate(-50 -60)"`,'#ffe066','#ff5a2a',.5,.2)+
  X.flat('path',`d="${inner}" transform="translate(50 64) scale(.84) translate(-50 -64)"`,'rgba(255,240,170,.65)')+
  X.gloss(38,44,4,6,-10,.55)+
  X.eye(41,58,4.8,{lid:.5,look:[.7,0]})+X.eye(59,58,4.8,{lid:.5,look:[.7,0]})+
  X.blush(34,66,66,4.4,.8)+X.mouth('smirk',50,70,5)+
  peach(X,84,30,8));
D('spicy2','On fire',X=>
  X.shadow(50,95,32)+
  X.clay('path',`d="${flame}"`,'#ffd84a','#ff2d3b',.5,.25)+
  X.flat('path',`d="${inner}"`,'rgba(255,236,150,.6)')+
  X.gloss(36,40,5,8,-10,.6)+
  X.eye(40,58,6,{kind:'heart'})+X.eye(60,58,6,{kind:'heart'})+
  X.blush(30,70,68,5,.9)+X.mouth('open',50,70,7)+
  `<path d="M52 76 q2 9 7 7 q2 -5 -1 -9" fill="#ff6f8e"/>`+
  X.drop(76,34,3.6)+X.drop(22,40,3)+
  egg(X,14,74,9,-30)+peach(X,88,74,8)+
  fan(X,82,52,10,30)+
  `<path d="M28 12 q-3 -5 1 -9 M70 10 q3 -5 -1 -9" stroke="#fff" stroke-width="2.2" stroke-linecap="round" fill="none" opacity=".6"/>`);
D('spicy3','Send help',X=>
  `<g opacity=".55">`+X.clay('path',`d="${flame}" transform="translate(50 50) scale(1.18) translate(-50 -50)"`,'#ff7a4a','#ff1f6e')+`</g>`+
  X.clay('path',`d="${flame}"`,'#ffe066','#f0104f',.5,.25)+
  X.flat('path',`d="${inner}"`,'rgba(255,236,150,.55)')+
  X.gloss(36,40,5,8,-10,.6)+
  X.eye(38,56,7.4,{kind:'heart'})+X.eye(62,56,7.4,{kind:'heart'})+X.brows(38,62,45,5,-18)+
  X.blush(28,72,68,6,1)+X.mouth('open',50,68,9)+
  `<path d="M53 76 q3 12 9 9 q3 -7 -1 -12" fill="#ff6f8e"/>`+
  X.drop(44,84,3,'#e8f6ff','#8fd1ff')+
  X.drop(80,30,4)+X.drop(18,36,3.4)+X.drop(86,48,2.6)+
  egg(X,12,72,10,-25)+peach(X,90,74,9)+egg(X,84,14,6,40)+peach(X,14,16,6)+
  fan(X,78,58,10,35)+
  X.heart(50,4,4.5)+X.spark(24,86,4,'#ffd84a')+X.spark(70,90,3.5,'#ffd84a')+
  `<path d="M30 6 q-4 -5 0 -10 M66 4 q4 -5 0 -10" stroke="#fff" stroke-width="2.6" stroke-linecap="round" fill="none" opacity=".7"/>`);
D('inlove','In love',X=>
  X.shadow(50,94,28)+
  X.clay('circle','cx="50" cy="56" r="32"','#ffc2dc','#ff4f93')+X.gloss(38,38,9,5)+
  X.eye(38,52,6.6,{kind:'heart'})+X.eye(62,52,6.6,{kind:'heart'})+
  X.mouth('smile',50,66,6)+X.blush(28,72,64,5,.8)+
  X.heart(16,22,6)+X.heart(84,16,7,'#ff7aa8')+X.heart(88,40,4,'#ffb0cc')+X.heart(28,8,3.6,'#ffb0cc'));
D('heartbroken','Heartbroken',X=>
  X.shadow(50,94,30)+
  X.clay('path','d="M50 88 C18 70 8 50 14 34 C20 18 40 16 50 32 C60 16 80 18 86 34 C92 50 82 70 50 88 Z"','#ff7a8e','#b3123a')+
  `<path d="M50 32 L44 46 L54 54 L46 66 L52 76" stroke="#5a0620" stroke-width="3" stroke-linejoin="round" stroke-linecap="round" fill="none"/>`+
  X.gloss(26,32,6,3.5)+
  X.eye(34,46,4.6,{look:[0,.6]})+X.eye(66,46,4.6,{look:[0,.6]})+X.brows(34,66,38,4,-18)+
  X.mouth('frown',62,60,4)+X.drop(30,56,3,'#ffffff','#7fd0ff')+
  `<g transform="rotate(-20 82 76)"><path d="M76 74 L82 96 L88 74 Z" fill="#e8a45a"/><path d="M77 78 L87 78 M78 84 L86 84" stroke="#b86e2c" stroke-width="1"/>`+X.clay('circle','cx="82" cy="70" r="7"','#fff0f6','#ff9ac0')+`</g>`);
D('hungover','Hungover',X=>
  X.shadow(50,94,30)+
  X.clay('path','d="M20 60 Q18 30 50 28 Q82 30 80 60 Q80 88 50 88 Q20 88 20 60 Z"','#e4f7b0','#8fbf4a')+
  X.clay('rect','x="30" y="14" width="40" height="20" rx="9" transform="rotate(-8 50 24)"','#bfe6ff','#3f8fe0')+
  `<path d="M38 20 l4 6 M50 18 l4 6 M60 17 l3 5" stroke="#fff" stroke-width="1.6" opacity=".6" stroke-linecap="round"/>`+
  X.gloss(30,44,5,3,-20,.5)+
  X.eye(38,54,5.4,{kind:'spiral'})+X.eye(62,54,5.4,{kind:'spiral'})+
  X.mouth('wavy',50,70,5)+
  X.spark(14,40,4,'#ffe066')+X.spark(86,46,3.5,'#ffe066')+X.spark(82,24,2.6,'#fff'));
})();
