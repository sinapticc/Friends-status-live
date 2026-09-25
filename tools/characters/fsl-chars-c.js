(function go(){if(!window.FSL)return setTimeout(go,15);const D=(k,l,f)=>window.FSL.def(k,l,f);
D('walking','Out walking',X=>
  X.shadow(52,92,36)+
  `<path d="M4 50 H16 M2 60 H12 M6 70 H14" stroke="#c8f542" stroke-width="3" stroke-linecap="round" opacity=".8"/>`+
  `<g transform="rotate(-10 52 64)">`+
  X.clay('rect','x="16" y="68" width="76" height="13" rx="6.5"','#ffffff','#b3b9d3')+
  X.flat('rect','x="16" y="76" width="76" height="5" rx="2.5"','#ff5ca8')+
  X.clay('path','d="M18 68 Q16 50 28 48 L40 45 Q46 30 60 32 Q68 34 70 44 Q82 49 89 58 Q93 66 88 68 Z"','#ff8cc4','#c8176a')+
  X.clay('path','d="M40 45 Q46 32 58 33 L60 44 Q50 44 44 50 Z"','#ffffff','#c3c8e2')+
  `<path d="M46 40 l6 3 M49 36 l6 3" stroke="#fff" stroke-width="2.4" stroke-linecap="round"/>`+
  `<path d="M24 60 Q40 52 58 58" stroke="#fff" stroke-width="3" stroke-linecap="round" fill="none" opacity=".85"/>`+
  X.gloss(66,40,4,2,-20,.7)+
  X.eye(66,54,4.4,{look:[1,0]})+X.eye(78,55,4.4,{look:[1,0]})+X.mouth('smile',73,62,3.6)+X.blush(62,86,61,3.2)+`</g>`);
D('dnd','Do not disturb',X=>{
  const pts=(r,cx=50,cy=46)=>Array.from({length:8},(_,i)=>{const a=Math.PI/8+i*Math.PI/4;return (cx+r*Math.cos(a)).toFixed(1)+','+(cy+r*Math.sin(a)).toFixed(1);}).join(' ');
  return X.shadow(50,95,20)+X.clay('rect','x="46" y="74" width="8" height="20" rx="3"','#d9dcea','#6f7590')+
  X.clay('polygon',`points="${pts(36)}" stroke-linejoin="round" stroke="#ff5c5c" stroke-width="6"`,'#ff6a6a','#a8122c')+
  `<polygon points="${pts(29)}" fill="none" stroke="#fff" stroke-width="3" stroke-linejoin="round" opacity=".9"/>`+
  X.gloss(34,26,9,4,-25,.6)+
  X.eye(39,45,5.6,{lid:.42})+X.eye(61,45,5.6,{lid:.42})+X.brows(39,61,36,6,24)+
  X.mouth('frown',50,60,7)+
  `<path d="M82 14 q4 -4 8 0 M86 8 q4 -4 8 0" stroke="#ff9aa8" stroke-width="2.2" stroke-linecap="round" fill="none"/>`;});
D('bored','Bored',X=>
  X.clay('ellipse','cx="50" cy="86" rx="42" ry="8"','#9ff7df','#1aa283')+
  X.clay('path','d="M14 86 Q22 80 22 62 Q22 32 50 32 Q78 32 78 62 Q78 80 86 86 Z"','#a8ffe6','#18a483')+
  X.clay('path','d="M24 70 Q22 84 26 88 Q30 84 28 72 Z"','#a8ffe6','#18a483')+
  X.clay('path','d="M70 72 Q72 88 70 94 Q64 90 66 74 Z"','#a8ffe6','#18a483')+
  X.clay('circle','cx="70" cy="96" r="3"','#a8ffe6','#18a483')+
  X.gloss(36,42,8,4)+
  X.eye(39,58,5.4,{lid:.56,look:[-.3,.5]})+X.eye(61,58,5.4,{lid:.56,look:[-.3,.5]})+
  X.mouth('flat',50,72,5));
D('free','Free · hit me up',X=>
  X.shadow(50,94,28)+
  `<path d="M28 52 Q14 42 16 24 M72 52 Q88 44 86 26" stroke="#7fcf1e" stroke-width="9" stroke-linecap="round" fill="none"/>`+
  X.clay('circle','cx="16" cy="22" r="7"','#e4ff7a','#6cc21a')+X.clay('circle','cx="86" cy="24" r="7"','#e4ff7a','#6cc21a')+
  `<path d="M4 16 q-2 6 0 12 M28 10 q3 5 2 11 M94 14 q3 6 1 12 M74 12 q-3 5 -2 10" stroke="#fff" stroke-width="2" stroke-linecap="round" fill="none" opacity=".7"/>`+
  X.clay('circle','cx="50" cy="58" r="30"','#eaff84','#5fb814')+
  X.gloss(38,40,9,5)+
  X.eye(39,52,6.6,{look:[0,-.3]})+X.eye(61,52,6.6,{look:[0,-.3]})+
  X.mouth('open',50,64,9)+X.blush(28,72,64,5,.6)+
  X.spark(12,48,4,'#fff')+X.spark(90,50,3.5,'#fff'));
D('custom','Custom',X=>
  X.shadow(50,92,28)+
  X.clay('path','d="M34 20 H66 Q86 20 86 42 Q86 64 66 64 H44 L28 80 L32 63 Q14 60 14 42 Q14 20 34 20 Z"','#ffffff','#b0a6f0')+
  X.gloss(28,30,8,3.5)+
  X.eye(40,40,5.2)+X.eye(60,40,5.2)+X.mouth('smile',50,50,4.5)+X.blush(32,68,48,4)+
  `<g transform="rotate(35 80 74)">`+X.clay('rect','x="75" y="58" width="10" height="28" rx="3"','#ffe07a','#e28b12')+
  X.flat('path','d="M75 86 H85 L80 95 Z"','#ffd9b0')+X.flat('path','d="M78.4 92 H81.6 L80 95 Z"','#1c1330')+X.flat('rect','x="75" y="56" width="10" height="5" rx="2"','#ff8cc4')+`</g>`);
})();
