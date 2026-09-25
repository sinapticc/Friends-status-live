(function go(){if(!window.FSL)return setTimeout(go,15);const D=(k,l,f)=>window.FSL.def(k,l,f);
D('driving','Driving',X=>
  X.shadow(50,92,38)+
  X.flat('rect','x="20" y="72" width="14" height="14" rx="4"','#1b1628')+X.flat('rect','x="66" y="72" width="14" height="14" rx="4"','#1b1628')+
  X.clay('path','d="M14 60 Q14 46 28 44 L34 28 Q37 20 46 20 H54 Q63 20 66 28 L72 44 Q86 46 86 60 V70 Q86 80 76 80 H24 Q14 80 14 70 Z"','#7ff0da','#0f8a78')+
  X.flat('path','d="M37 30 Q39 25 46 25 H54 Q61 25 63 30 L67 44 H33 Z"',X.lg('#d8f7ff','#63a9d1'))+
  X.gloss(42,30,5,2,-15,.8)+X.gloss(26,50,6,2.5,-10,.5)+
  `<g fill="#15101f"><path d="M34 34 H48 Q48 43 41 43 Q34 43 34 34 Z"/><path d="M52 34 H66 Q66 43 59 43 Q52 43 52 34 Z"/></g><path d="M47 35 Q50 33 53 35" stroke="#15101f" stroke-width="2" fill="none"/>`+
  `<path d="M37 36 l4 3 M55 36 l4 3" stroke="#fff" stroke-width="1.6" stroke-linecap="round" opacity=".8"/>`+
  X.mouth('smile',50,63,6)+
  X.clay('circle','cx="26" cy="59" r="6"','#fffbe0','#ffcf3a')+X.clay('circle','cx="74" cy="59" r="6"','#fffbe0','#ffcf3a')+
  X.flat('rect','x="38" y="70" width="24" height="5" rx="2.5"','#0c6d5f'));
D('gaming','Gaming',X=>
  X.shadow(50,90,38)+
  X.clay('path','d="M22 38 Q24 28 36 28 H64 Q76 28 78 38 L88 66 Q91 80 80 80 Q72 80 68 70 L65 64 H35 L32 70 Q28 80 20 80 Q9 80 12 66 Z"','#b49bff','#4a28b8')+
  X.gloss(34,33,8,2.5,-8,.65)+
  X.flat('path','d="M24 44 h5 v-5 h5 v5 h5 v5 h-5 v5 h-5 v-5 h-5 Z"','#231a4d')+
  `<circle cx="72" cy="40" r="3.2" fill="#ff5c7a"/><circle cx="79" cy="46" r="3.2" fill="#ffd23a"/><circle cx="65" cy="46" r="3.2" fill="#4fd1ff"/><circle cx="72" cy="52" r="3.2" fill="#7cf06a"/>`+
  X.eye(43,50,5,{lid:.3})+X.eye(57,50,5,{lid:.3})+X.brows(43,57,41,4.5,20)+
  X.mouth('flat',50,60,4)+`<path d="M53 60 q2 5 5 3 q-1 -3 -2 -3" fill="#ff6f8e"/>`+
  X.drop(40,20,3)+`<path d="M14 22 l5 4 M22 16 l2 6" stroke="#ffd23a" stroke-width="2" stroke-linecap="round"/>`);
D('partying','Partying',X=>{
  const pid=X.u+'p';
  const tiles=`<pattern id="${pid}" width="7" height="7" patternUnits="userSpaceOnUse"><rect width="7" height="7" fill="none" stroke="#5c5f86" stroke-width=".9" opacity=".55"/></pattern>`;
  let conf=''; [[12,24,'#ff5ca8',30],[86,18,'#c8f542',-20],[90,60,'#4fd1ff',50],[10,66,'#ffd23a',-40],[20,8,'#4fd1ff',10],[78,86,'#ff5ca8',60],[24,88,'#c8f542',20]].forEach(([x,y,c,r])=>conf+=`<rect x="${x-2.5}" y="${y-1.2}" width="5" height="2.4" rx="1" fill="${c}" transform="rotate(${r} ${x} ${y})"/>`);
  return `<defs>${tiles}</defs>`+X.shadow(50,94,26)+`<path d="M50 0 V20" stroke="#8b8fb3" stroke-width="1.6"/>`+
  X.clay('circle','cx="50" cy="54" r="34"','#ffffff','#6d739c')+
  `<circle cx="50" cy="54" r="34" fill="url(#${pid})"/>`+
  `<g opacity=".9"><rect x="28" y="36" width="7" height="7" fill="#ff9ad0"/><rect x="63" y="29" width="7" height="7" fill="#9ff1ff"/><rect x="70" y="57" width="7" height="7" fill="#c8f542"/><rect x="21" y="57" width="7" height="7" fill="#b49bff"/><rect x="49" y="78" width="7" height="7" fill="#ffd23a"/></g>`+
  X.gloss(36,36,9,5,-30,.9)+
  X.eye(40,50,6.5,{kind:'happy'})+X.eye(60,50,6.5,{kind:'happy'})+
  X.mouth('open',50,60,9)+X.blush(30,70,60,5)+
  X.spark(84,36,5,'#fff')+X.spark(14,42,4,'#ffd23a')+X.spark(70,10,3.5,'#fff')+conf;});
D('showering','Showering',X=>{
  let dots='';[[36,20],[48,17],[56,24],[42,27]].forEach(([x,y])=>dots+=`<circle cx="${x}" cy="${y}" r="1.8" fill="#fff" opacity=".85"/>`);
  return X.shadow(54,92,32)+
  X.clay('path','d="M76 58 Q90 44 88 36 Q84 50 72 52 Z"','#ffe56b','#e89a12')+
  X.clay('ellipse','cx="54" cy="68" rx="32" ry="20"','#ffe872','#e8950f')+
  X.clay('ellipse','cx="62" cy="68" rx="13" ry="8"','#ffd84a','#d9860a')+
  X.clay('circle','cx="42" cy="42" r="19"','#ffec80','#eca016')+
  X.gloss(33,36,5,3)+X.gloss(40,58,8,3,-10,.5)+
  X.clay('path','d="M16 46 Q22 38 32 44 Q26 54 16 50 Z"','#ffab5c','#e35a12')+
  X.eye(38,42,4.2)+X.eye(51,42,4.2)+X.blush(33,56,50,3.6)+
  X.clay('path','d="M22 34 Q24 12 43 12 Q62 12 63 34 Q52 29 43 29 Q32 29 22 34 Z"','#ffa6d8','#d9368c')+dots+
  `<path d="M21 34 q3.5 -3 7 -1.5 t7 -1.5 t8 0 t7 1.5 t7 2" stroke="#fff" stroke-width="3" stroke-linecap="round" fill="none"/>`+
  X.drop(76,10,3.4)+X.drop(86,24,2.8)+X.drop(70,26,2.4)+
  `<circle cx="18" cy="80" r="4" fill="none" stroke="#bfe9ff" stroke-width="1.4"/><circle cx="12" cy="70" r="2.5" fill="none" stroke="#bfe9ff" stroke-width="1.2"/>`;});
D('coffee','Coffee break',X=>
  X.shadow(50,92,32)+
  `<path d="M36 22 q-4 -6 0 -11 t0 -10 M48 22 q-4 -6 0 -11 t0 -10 M60 22 q-4 -6 0 -11 t0 -10" stroke="#fff" stroke-width="2.6" stroke-linecap="round" fill="none" opacity=".55"/>`+
  `<path d="M70 42 Q86 42 86 56 Q86 70 70 70" stroke="#c9502f" stroke-width="9" stroke-linecap="round" fill="none"/>`+
  X.clay('rect','x="22" y="28" width="50" height="58" rx="13"','#ffb08a','#b9401f')+
  X.flat('ellipse','cx="47" cy="31" rx="22" ry="5"','#5a2a14')+X.flat('ellipse','cx="44" cy="30.5" rx="9" ry="2"','#c9895a')+
  X.gloss(32,40,5,8,-5,.5)+
  X.eye(38,54,5,{kind:'closed'})+X.eye(56,54,5,{kind:'closed'})+
  X.blush(31,63,62,4.6,.7)+X.mouth('smile',47,64,4.5));
D('sick','Sick',X=>
  X.shadow(50,94,34)+
  X.clay('rect','x="33" y="6" width="34" height="70" rx="17"','#ffffff','#b7bfd8')+
  X.flat('rect','x="47" y="44" width="6" height="20" rx="3"','#ff3d5c')+
  `<path d="M58 22 h5 M58 30 h5 M58 38 h5" stroke="#b7bfd8" stroke-width="1.4" stroke-linecap="round"/>`+
  X.gloss(41,14,3,6,-10,.8)+
  X.eye(43,26,4.4,{look:[0,.6]})+X.eye(57,26,4.4,{look:[0,.6]})+X.brows(43,57,18.5,3.6,-16)+
  X.blush(39,61,33,4.5,.85)+X.mouth('wavy',50,38,3.6)+
  X.drop(69,12,2.8)+
  X.clay('path','d="M16 56 Q50 42 84 56 L88 86 Q50 98 12 86 Z"','#8ab8ff','#2a55c0')+
  `<path d="M22 60 L20 88 M36 53 L34 93 M52 50 V95 M68 53 L70 93 M80 58 L82 88 M15 70 Q50 58 86 70 M13 80 Q50 70 87 80" stroke="#fff" stroke-width="1.4" opacity=".28" fill="none"/>`+
  X.gloss(30,58,8,2.5,-15,.5)+
  X.clay('circle','cx="40" cy="60" r="5"','#ffffff','#b7bfd8')+X.clay('circle','cx="60" cy="60" r="5"','#ffffff','#b7bfd8'));
})();
