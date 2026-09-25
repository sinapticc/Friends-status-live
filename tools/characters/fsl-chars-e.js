(function go(){if(!window.FSL)return setTimeout(go,15);const D=(k,l,f)=>window.FSL.def(k,l,f);
D('angry','Angry',X=>
  X.shadow(50,94,32)+
  `<g fill="#fff" opacity=".85"><circle cx="30" cy="16" r="6"/><circle cx="37" cy="10" r="5"/><circle cx="70" cy="14" r="6"/><circle cx="64" cy="8" r="4.5"/></g>`+
  X.clay('path','d="M16 66 Q14 34 50 30 Q86 34 84 66 Q84 90 50 90 Q16 90 16 66 Z"','#ff7a5c','#c4122e')+
  X.gloss(32,42,7,4)+
  X.eye(38,58,5.6,{lid:.35})+X.eye(62,58,5.6,{lid:.35})+X.brows(38,62,48,6.5,26)+
  X.mouth('zig',50,74,6)+
  `<path d="M78 34 l6 -4 M84 42 l6 0 M80 26 l3 -6" stroke="#ffcf3a" stroke-width="2.4" stroke-linecap="round"/>`);
D('crying','Crying',X=>
  X.shadow(50,95,26,.2)+
  X.clay('path','d="M22 62 C8 62 8 42 22 42 C22 26 42 22 50 32 C58 20 80 24 78 42 C92 42 92 62 78 62 Z"','#e0e6ff','#7f8cc4')+
  X.gloss(30,40,6,3)+
  X.eye(40,48,5,{look:[0,.6]})+X.eye(60,48,5,{look:[0,.6]})+X.brows(40,60,40,4,-18)+
  X.mouth('frown',50,56,4.5)+
  `<path d="M38 53 Q36 60 38 66 M62 53 Q64 60 62 66" stroke="#6fc3ff" stroke-width="3" stroke-linecap="round" fill="none"/>`+
  X.drop(28,76,3.4)+X.drop(44,84,3)+X.drop(60,76,3.4)+X.drop(74,86,2.8)+X.drop(36,94,2.2));
D('date','On a date',X=>
  X.shadow(46,94,28)+
  X.clay('circle','cx="44" cy="54" r="30"','#ffd9b8','#e98a5a')+X.gloss(32,36,8,4.5)+
  X.eye(34,50,5.4)+X.eye(54,50,5.4)+X.mouth('smile',44,62,5)+X.blush(26,62,60,5,.8)+
  `<path d="M36 82 l8 -4 l8 4 l-8 4 Z" fill="#1c1330"/><circle cx="44" cy="82" r="2.4" fill="#ff4d7d"/>`+
  `<path d="M70 92 L78 66 L92 70 Z" fill="#fff4e6" stroke="#f0c8a0" stroke-width="1.2"/>`+
  `<path d="M80 70 Q76 56 72 48 M82 70 Q84 54 86 46 M84 70 Q92 58 94 52" stroke="#4fbf5a" stroke-width="2" fill="none"/>`+
  X.clay('circle','cx="72" cy="46" r="6"','#ffb0cc','#ff3d7f')+X.clay('circle','cx="86" cy="42" r="6"','#fff0a0','#ffb13d')+X.clay('circle','cx="95" cy="52" r="5"','#d0c0ff','#8a5cff')+
  X.clay('circle','cx="68" cy="72" r="5"','#ffd9b8','#e98a5a'));
D('shopping','Shopping',X=>
  X.shadow(50,94,30)+
  `<path d="M36 32 Q36 12 50 12 Q64 12 64 32" stroke="#c2185b" stroke-width="5" fill="none" stroke-linecap="round"/>`+
  X.clay('path','d="M20 30 H80 L86 88 Q86 92 82 92 H18 Q14 92 14 88 Z"','#ff9cc8','#d81b72')+
  X.gloss(28,42,6,3.5)+
  `<g fill="#15101f"><path d="M28 50 H46 Q46 62 37 62 Q28 62 28 50 Z"/><path d="M54 50 H72 Q72 62 63 62 Q54 62 54 50 Z"/></g><path d="M46 52 Q50 49 54 52" stroke="#15101f" stroke-width="2" fill="none"/><path d="M31 53 l4 3 M57 53 l4 3" stroke="#fff" stroke-width="1.6" stroke-linecap="round" opacity=".8"/>`+
  X.mouth('smirk',50,72,5)+
  `<g transform="rotate(20 82 30)"><rect x="76" y="24" width="14" height="10" rx="2" fill="#ffe066"/><circle cx="79" cy="29" r="1.4" fill="#1c1330"/></g>`+
  X.spark(12,22,4,'#fff')+X.spark(90,60,3,'#ffe066'));
D('movie','Watching a movie',X=>
  X.shadow(50,94,28)+
  `<g>`+[[30,26,9],[44,18,10],[58,20,9],[70,28,8],[38,32,8],[52,30,9],[64,34,7],[26,36,6]].map(([x,y,r])=>X.clay('circle',`cx="${x}" cy="${y}" r="${r}"`,'#fffbe6','#f2c46a')).join('')+`</g>`+
  X.clay('path','d="M20 38 H80 L72 92 H28 Z"','#ffffff','#c8cde0')+
  `<path d="M30 38 L35 92 M44 38 L45 92 M56 38 L55 92 M70 38 L65 92" stroke="#ff3d5c" stroke-width="7"/>`+
  X.flat('rect','x="18" y="36" width="64" height="7" rx="3.5"','#ff3d5c')+
  X.eye(40,60,6,{look:[0,-.4]})+X.eye(60,60,6,{look:[0,-.4]})+
  X.mouth('o',50,74,5)+X.gloss(28,50,3,6,-8,.6));
D('traveling','Traveling',X=>
  X.shadow(50,94,32)+
  `<path d="M40 24 V16 Q40 12 44 12 H56 Q60 12 60 16 V24" stroke="#2b2240" stroke-width="4.5" fill="none"/>`+
  X.clay('rect','x="18" y="24" width="64" height="62" rx="12"','#7fe7e0','#11888a')+
  X.flat('rect','x="18" y="40" width="64" height="4"','#0b6f72')+
  X.gloss(28,32,7,3)+
  `<circle cx="70" cy="34" r="6" fill="#ffcf3a"/><path d="M24 72 l8 -8 l8 8 Z" fill="#ff5ca8"/><rect x="62" y="68" width="14" height="10" rx="3" fill="#b49bff" transform="rotate(-12 69 73)"/><path d="M28 30 l3 2 l-1 3 l3 1" stroke="#fff" stroke-width="1.4" fill="none"/>`+
  X.eye(40,58,5)+X.eye(60,58,5)+X.mouth('open',50,66,5)+X.blush(32,68,66,4)+
  `<circle cx="28" cy="90" r="4" fill="#1c1330"/><circle cx="72" cy="90" r="4" fill="#1c1330"/>`);
D('cooking','Cooking',X=>
  X.shadow(50,94,34)+
  `<path d="M36 24 q-3 -6 0 -10 M50 22 q-3 -6 0 -10 M64 24 q-3 -6 0 -10" stroke="#fff" stroke-width="2.4" stroke-linecap="round" fill="none" opacity=".5"/>`+
  X.clay('rect','x="4" y="52" width="14" height="6" rx="3"','#9aa1b7','#4b5066')+X.clay('rect','x="82" y="52" width="14" height="6" rx="3"','#9aa1b7','#4b5066')+
  X.clay('path','d="M14 46 H86 V74 Q86 90 70 90 H30 Q14 90 14 74 Z"','#dfe4f0','#6e7590')+
  X.flat('rect','x="12" y="42" width="76" height="8" rx="4"','#aab1c6')+
  X.gloss(26,56,6,3,-10,.6)+
  X.clay('path','d="M30 42 Q22 36 28 28 Q30 18 40 22 Q46 12 56 20 Q66 16 70 26 Q78 30 70 42 Z"','#ffffff','#c9cee0')+
  X.eye(40,62,5,{kind:'happy'})+X.eye(60,62,5,{kind:'happy'})+X.mouth('smile',50,72,5)+X.blush(30,70,70,4)+
  `<path d="M78 40 L92 12" stroke="#c98a4a" stroke-width="4" stroke-linecap="round"/><ellipse cx="93" cy="10" rx="4" ry="5" fill="#c98a4a"/>`);
D('meditating','Meditating',X=>
  `<circle cx="50" cy="52" r="44" fill="${X.g('rgba(200,245,66,.35)','rgba(200,245,66,0)',.5,.5)}"/>`+
  `<ellipse cx="50" cy="12" rx="16" ry="4" fill="none" stroke="#fff3a0" stroke-width="3"/>`+
  X.shadow(50,92,26)+
  X.clay('path','d="M22 84 Q18 70 30 66 L70 66 Q82 70 78 84 Q50 94 22 84 Z"','#c9b6ff','#6a3cff')+
  X.clay('circle','cx="50" cy="48" r="24"','#e4d8ff','#8a6cff')+
  X.gloss(40,34,6,3.5)+
  X.eye(42,50,4.4,{kind:'closed'})+X.eye(58,50,4.4,{kind:'closed'})+X.mouth('smile',50,58,3.6)+X.blush(36,64,56,3.6)+
  X.clay('circle','cx="26" cy="76" r="5"','#e4d8ff','#8a6cff')+X.clay('circle','cx="74" cy="76" r="5"','#e4d8ff','#8a6cff')+
  X.spark(14,34,4,'#fff3a0')+X.spark(86,30,4,'#fff3a0')+X.spark(80,60,2.6,'#fff'));
})();
