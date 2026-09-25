(function go(){if(!window.FSL)return setTimeout(go,15);const D=(k,l,f)=>window.FSL.def(k,l,f);
D('hookah','Hookah break',X=>
  `<g fill="#e9e4f5" opacity=".75"><circle cx="70" cy="22" r="8"/><circle cx="80" cy="14" r="6"/><circle cx="62" cy="12" r="5"/><circle cx="86" cy="26" r="4"/></g><circle cx="74" cy="4" r="3" fill="none" stroke="#e9e4f5" stroke-width="1.6" opacity=".7"/>`+
  X.shadow(46,94,30)+
  X.clay('path','d="M14 70 Q12 38 44 36 Q76 38 74 70 Q74 90 44 90 Q14 90 14 70 Z"','#b4f5de','#1f9e7c')+
  X.gloss(28,48,6,3.5)+
  X.eye(34,60,5,{lid:.55})+X.eye(54,60,5,{lid:.55})+X.mouth('smile',44,72,4.5)+X.blush(26,62,70,4)+
  `<path d="M52 74 Q66 76 66 60 Q66 44 62 34" stroke="#b07cff" stroke-width="3.4" fill="none" stroke-linecap="round"/><circle cx="52" cy="74" r="3" fill="#ffcf3a"/>`);
D('cleaning','Cleaning',X=>
  X.shadow(50,95,30)+
  `<path d="M50 4 V40" stroke="#c98a4a" stroke-width="6" stroke-linecap="round"/>`+
  X.clay('rect','x="30" y="36" width="40" height="12" rx="5"','#7fb2ff','#2a55c0')+
  X.clay('path','d="M24 46 H76 Q84 70 80 88 Q70 94 62 88 Q58 94 50 90 Q42 94 38 88 Q30 94 20 88 Q16 70 24 46 Z"','#f2eee6','#a39c90')+
  `<path d="M32 60 Q30 76 30 88 M44 60 Q42 78 44 90 M56 60 Q58 78 56 90 M68 60 Q70 76 70 88" stroke="#b9b1a4" stroke-width="1.6" fill="none"/>`+
  X.eye(40,58,4.8,{lid:.55})+X.eye(60,58,4.8,{lid:.55})+X.mouth('flat',50,68,4)+
  X.drop(80,30,3)+`<g fill="none" stroke="#bfe9ff" stroke-width="1.6"><circle cx="14" cy="80" r="5"/><circle cx="88" cy="74" r="4"/><circle cx="12" cy="64" r="2.6"/></g>`);
D('traffic','Stuck in traffic',X=>
  X.shadow(50,92,38)+
  X.flat('rect','x="20" y="72" width="14" height="14" rx="4"','#1b1628')+X.flat('rect','x="66" y="72" width="14" height="14" rx="4"','#1b1628')+
  X.clay('path','d="M14 60 Q14 46 28 44 L34 28 Q37 20 46 20 H54 Q63 20 66 28 L72 44 Q86 46 86 60 V70 Q86 80 76 80 H24 Q14 80 14 70 Z"','#ff7a6a','#b3122e')+
  X.flat('path','d="M37 30 Q39 25 46 25 H54 Q61 25 63 30 L67 44 H33 Z"',X.lg('#ffe0d8','#e0827a'))+
  X.eye(42,37,4.6,{lid:.35})+X.eye(58,37,4.6,{lid:.35})+X.brows(42,58,30,4.4,26)+
  X.mouth('zig',50,64,6)+
  X.clay('circle','cx="26" cy="59" r="5.5"','#fffbe0','#ffcf3a')+X.clay('circle','cx="74" cy="59" r="5.5"','#fffbe0','#ffcf3a')+
  `<path d="M6 30 l-5 -4 M4 40 h-6 M92 28 l5 -4 M94 38 h6" stroke="#ffcf3a" stroke-width="2.6" stroke-linecap="round"/>`+
  `<g fill="#fff" font-family="Arial Black,sans-serif" font-weight="900" font-size="13"><text x="80" y="16">!!</text></g>`);
D('lowbattery','Low battery',X=>
  X.shadow(50,95,24)+
  X.clay('rect','x="28" y="8" width="44" height="82" rx="10"','#4a4266','#15101f')+
  X.flat('rect','x="32" y="14" width="36" height="70" rx="6"',X.lg('#2a2344','#171229'))+
  X.flat('rect','x="44" y="10" width="12" height="3" rx="1.5"','#0b0914')+
  `<rect x="38" y="22" width="22" height="11" rx="2.5" fill="none" stroke="#ff4d5e" stroke-width="2"/><rect x="60" y="25" width="2.6" height="5" rx="1" fill="#ff4d5e"/><rect x="40.5" y="24.5" width="4" height="6" rx="1" fill="#ff4d5e"/>`+
  X.eye(42,50,4.2,{lid:.6,color:'#bfe9ff'})+X.eye(58,50,4.2,{lid:.6,color:'#bfe9ff'})+
  `<path d="M44 64 Q50 60 56 64" stroke="#bfe9ff" stroke-width="2.2" fill="none" stroke-linecap="round"/>`+
  X.drop(74,18,3)+X.gloss(34,16,3,5,-8,.35));
D('overthinking','Overthinking',X=>
  X.shadow(46,95,26)+
  X.clay('path','d="M48 4 C66 2 88 8 88 24 C88 38 70 42 56 40 C40 42 26 34 30 20 C32 10 38 5 48 4 Z"','#ffffff','#c9c4ec')+
  `<path d="M40 22 C44 10 58 30 62 16 C66 6 76 30 70 30 C60 32 52 12 48 26 C46 32 70 14 78 22" stroke="#6a3cff" stroke-width="2" fill="none" stroke-linecap="round"/>`+
  `<circle cx="32" cy="46" r="4" fill="#fff"/><circle cx="26" cy="54" r="2.5" fill="#fff"/>`+
  X.clay('circle','cx="40" cy="72" r="22"','#ffe07a','#e5901a')+
  X.gloss(32,60,5,3)+
  X.eye(33,70,4.6,{look:[.5,-.8]})+X.eye(49,70,4.6,{look:[.5,-.8]})+X.brows(33,49,62,3.6,-14)+X.mouth('wavy',41,82,3.6));
D('maincharacter','Main character',X=>
  `<circle cx="50" cy="54" r="46" fill="${X.g('rgba(255,215,90,.5)','rgba(255,120,200,0)',.5,.5)}"/>`+
  X.shadow(50,94,26)+
  X.clay('circle','cx="50" cy="58" r="28"','#ffd2f0','#c24bd6')+X.gloss(40,42,7,4)+
  X.clay('path','d="M34 34 L38 20 L46 28 L50 16 L54 28 L62 20 L66 34 Z"','#ffe98a','#e0a012')+
  `<circle cx="50" cy="26" r="2" fill="#ff5ca8"/>`+
  X.eye(40,56,5.6,{kind:'star',color:'#1c1330'})+X.eye(60,56,5.6,{kind:'star',color:'#1c1330'})+
  X.mouth('smirk',50,68,5)+X.blush(32,68,64,4.4,.7)+
  X.spark(12,30,5,'#fff')+X.spark(88,24,6,'#ffe98a')+X.spark(86,70,3.6,'#fff')+X.spark(14,72,3,'#ffe98a'));
D('busy','Busy',X=>
  X.shadow(50,93,28)+
  X.clay('rect','x="20" y="24" width="60" height="62" rx="22"','#e9e6f2','#9892b0')+X.gloss(32,34,7,3.5)+
  X.eye(40,54,4.6)+X.eye(60,54,4.6)+X.mouth('flat',50,66,4)+
  X.clay('circle','cx="76" cy="26" r="13"','#ffffff','#b9bfd6')+`<path d="M76 18 V26 L82 30" stroke="#1c1330" stroke-width="2.6" stroke-linecap="round" fill="none"/>`);
D('period2','Chocolate mode',X=>
  X.shadow(50,94,30)+
  X.clay('rect','x="28" y="24" width="44" height="66" rx="8"','#8a4a28','#3a190b')+
  `<path d="M28 46 H72 M28 68 H72 M50 24 V90" stroke="#2a1006" stroke-width="1.6" opacity=".6"/>`+
  X.flat('path','d="M28 60 L72 52 V90 Q72 94 68 94 H32 Q28 94 28 90 Z"','#d9dbe8')+X.flat('path','d="M28 60 L72 52 V58 L28 66 Z"','#b3b6c8')+
  X.gloss(36,30,5,2.5,-10,.4)+
  X.eye(40,38,4.6,{kind:'happy',color:'#fff4e0'})+X.eye(60,38,4.6,{kind:'happy',color:'#fff4e0'})+
  `<path d="M44 50 Q50 45 56 50" stroke="#fff4e0" stroke-width="2.4" fill="none" stroke-linecap="round"/>`+
  X.blush(34,66,46,4,.8)+X.heart(14,20,5)+X.heart(86,30,4,'#ffb0cc')+X.heart(84,80,3.4,'#ff7aa8'));
D('period3','Don’t talk to me',X=>
  X.shadow(50,94,32)+
  X.clay('path','d="M20 84 Q14 60 20 44 Q28 26 50 26 Q72 26 80 44 Q86 60 80 84 Z"','#ff9cb6','#c2204d')+
  X.clay('path','d="M14 86 Q14 58 50 56 Q86 58 86 86 Q50 96 14 86 Z"','#b9a2ff','#5a36c8')+
  `<path d="M22 66 Q50 58 78 66 M16 78 Q50 70 84 78" stroke="#fff" stroke-width="1.6" opacity=".3" fill="none"/>`+
  X.gloss(32,36,6,3)+
  X.eye(38,44,5,{lid:.5})+X.eye(62,44,5,{lid:.5})+X.brows(38,62,36,5,20)+X.mouth('flat',50,52,4)+
  `<g fill="#fff" opacity=".85"><circle cx="16" cy="18" r="5"/><circle cx="22" cy="12" r="4"/><circle cx="84" cy="16" r="5"/></g>`);
D('football','Watching football',X=>
  X.shadow(50,94,34)+
  X.clay('rect','x="10" y="10" width="80" height="54" rx="8"','#4a4266','#15101f')+
  X.flat('rect','x="15" y="15" width="70" height="44" rx="4"',X.lg('#4fcf6a','#1c8a3a'))+
  `<path d="M50 15 V59 M15 37 H85" stroke="#fff" stroke-width="1" opacity=".5"/><circle cx="50" cy="37" r="7" fill="none" stroke="#fff" stroke-width="1" opacity=".5"/>`+
  X.eye(38,34,5,{look:[0,0],color:'#10331c'})+X.eye(62,34,5,{color:'#10331c'})+X.mouth('open',50,46,5)+
  X.flat('rect','x="40" y="64" width="20" height="8"','#2b2440')+
  X.clay('circle','cx="72" cy="78" r="14"','#ffffff','#b9bfd6')+
  `<path d="M72 71 l6 4 l-2 7 h-8 l-2 -7 Z" fill="#1c1330"/><path d="M72 64 V71 M78 75 L85 72 M76 82 L80 90 M68 82 L64 90 M66 75 L59 72" stroke="#1c1330" stroke-width="1.6"/>`+
  X.spark(16,78,4,'#ffe066')+`<path d="M22 90 l4 -8 M30 92 l2 -6" stroke="#ffe066" stroke-width="2" stroke-linecap="round"/>`);
D('barber','At the barber',X=>
  X.shadow(50,94,32)+
  X.clay('path','d="M12 90 Q14 60 50 58 Q86 60 88 90 Z"','#ffffff','#b7bfd8')+
  `<path d="M26 90 L30 70 M50 90 V64 M74 90 L70 70" stroke="#d6dcf0" stroke-width="1.6"/>`+
  X.clay('circle','cx="50" cy="42" r="24"','#ffd0a8','#d9885a')+
  X.clay('path','d="M26 40 Q26 16 50 16 Q74 16 74 40 Q64 28 50 30 Q36 28 26 40 Z"','#4a3428','#1a0f08')+
  X.eye(42,44,4.6,{look:[-.6,-.6]})+X.eye(58,44,4.6,{look:[-.6,-.6]})+X.mouth('frown',50,54,3.6)+
  `<g transform="rotate(-30 80 20)"><circle cx="74" cy="30" r="4" fill="none" stroke="#9aa1b7" stroke-width="2.2"/><circle cx="86" cy="30" r="4" fill="none" stroke="#9aa1b7" stroke-width="2.2"/><path d="M76 27 L84 6 M84 27 L76 6" stroke="#dfe4f0" stroke-width="2.6" stroke-linecap="round"/></g>`+
  `<path d="M20 20 l2 3 M16 28 l3 1 M88 44 l3 2" stroke="#4a3428" stroke-width="1.6" stroke-linecap="round"/>`);
D('beard','Beard trim',X=>
  X.shadow(50,94,28)+
  X.clay('circle','cx="46" cy="46" r="28"','#ffd0a8','#d9885a')+
  X.clay('path','d="M20 48 Q22 86 46 88 Q70 86 72 48 Q66 62 46 62 Q26 62 20 48 Z"','#6a4a38','#2a170c')+
  X.flat('path','d="M34 58 Q46 52 58 58 Q46 62 34 58 Z"','#2a170c')+
  X.gloss(36,30,6,3.5)+
  X.eye(36,42,4.6,{look:[.8,.4]})+X.eye(56,42,4.6,{look:[.8,.4]})+X.brows(36,56,34,4.4,-6)+
  `<g transform="rotate(-35 80 66)">`+X.clay('rect','x="74" y="50" width="12" height="34" rx="5"','#4a4266','#15101f')+X.flat('rect','x="73" y="46" width="14" height="6" rx="2"','#c8cde0')+`<circle cx="80" cy="64" r="2.2" fill="#c8f542"/></g>`+
  `<path d="M62 70 l3 2 M66 64 l3 0 M60 76 l2 3" stroke="#2a170c" stroke-width="1.6" stroke-linecap="round"/>`);
})();
