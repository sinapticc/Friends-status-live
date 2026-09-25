(function go(){if(!window.FSL)return setTimeout(go,15);const D=(k,l,f)=>window.FSL.def(k,l,f);
D('toilet','On the toilet',X=>
  X.shadow(50,93,30)+
  X.clay('rect','x="60" y="26" width="26" height="36" rx="7"','#ffffff','#aab2cf')+
  X.clay('path','d="M32 70 H70 L63 90 H40 Z"','#ffffff','#a9b0cc')+
  X.clay('ellipse','cx="50" cy="70" rx="29" ry="8"','#ffffff','#b7bdd6')+
  X.flat('ellipse','cx="50" cy="69" rx="21" ry="4.5"','#8b93b3')+
  X.clay('ellipse','cx="46" cy="60" rx="25" ry="13"','#c58a5c','#5b331c')+
  X.clay('ellipse','cx="46" cy="45" rx="19" ry="11"','#cf9667','#6a3c22')+
  X.clay('path','d="M34 38 C35 26 48 22 53 13 C57 22 62 30 58 38 Z"','#d9a274','#6e3e22')+
  X.gloss(38,40,6,3)+X.gloss(34,55,6,2.5,-15,.5)+X.gloss(44,24,2.5,4,20,.6)+
  X.eye(38,48,5.2,{lid:.46,look:[.8,.6]})+X.eye(52,48,5.2,{lid:.46,look:[.8,.6]})+
  X.blush(31,59,56,4.5)+X.mouth('smirk',45,57,4.4)+
  `<g transform="rotate(-14 66 56)">`+X.clay('rect','x="60" y="44" width="13" height="22" rx="3.5"','#4a4266','#15101f')+
  X.flat('rect','x="62" y="46.5" width="9" height="15" rx="1.8"',X.lg('#9ff1ff','#5e7bff'))+`</g>`+
  X.clay('circle','cx="62" cy="63" r="4.2"','#cf9667','#6a3c22'));
D('sleeping','Sleeping',X=>
  X.shadow(50,86,32,.25)+
  X.clay('path','d="M22 76 C8 76 8 56 23 55 C21 40 40 34 49 45 C55 32 78 34 77 51 C92 51 94 76 78 76 Z"','#ffffff','#a79ff0')+
  X.gloss(32,55,7,3.5)+X.gloss(62,44,6,2.5,-10,.6)+
  X.clay('path','d="M36 46 C38 28 58 18 72 22 C80 25 86 33 88 43 C80 37 71 36 66 44 Z"','#7d95ff','#2a36b0')+
  X.clay('rect','x="33" y="41" width="36" height="8" rx="4"','#ffffff','#c3c8e8')+
  X.clay('circle','cx="88" cy="45" r="6"','#ffffff','#bfc4e6')+
  X.gloss(56,27,5,2,-20,.5)+
  X.eye(42,60,5,{kind:'closed'})+X.eye(58,60,5,{kind:'closed'})+
  X.blush(35,65,66,4.5)+X.mouth('o',50,68,4)+
  X.z(76,14,8)+X.z(88,4,5.5)+X.z(68,4,4.5,'#b9afff'));
D('eating','Eating',X=>
  X.shadow(50,94,30)+
  X.clay('circle','cx="50" cy="50" r="33"','#ffe07a','#e97c1c')+
  X.gloss(36,32,9,5)+
  X.eye(38,40,6.2,{look:[0,.5]})+X.eye(62,40,6.2,{look:[0,.5]})+
  X.blush(26,74,56,6.5,.7)+
  X.clay('path','d="M30 64 Q30 48 52 48 Q74 48 74 64 Z"','#ffc25e','#b9601c')+
  `<ellipse cx="44" cy="54" rx="1.6" ry="1" fill="#fff5d6"/><ellipse cx="53" cy="52" rx="1.6" ry="1" fill="#fff5d6"/><ellipse cx="61" cy="56" rx="1.6" ry="1" fill="#fff5d6"/>`+
  `<path d="M28 65 q4 -3 8 0 t8 0 t8 0 t8 0 t8 0 t8 0 v3 h-48 Z" fill="#6fd64b"/>`+
  `<path d="M34 67 H72 L62 74 Z" fill="#ffcf33"/>`+
  X.clay('rect','x="29" y="67" width="46" height="8" rx="4"','#8a4a28','#3a190b')+
  X.clay('rect','x="31" y="75" width="42" height="8" rx="4"','#ffc25e','#b9601c')+
  X.clay('circle','cx="28" cy="72" r="5.5"','#ffe07a','#e97c1c')+X.clay('circle','cx="76" cy="72" r="5.5"','#ffe07a','#e97c1c')+
  `<circle cx="20" cy="86" r="1.4" fill="#e9a04a"/><circle cx="82" cy="88" r="1.2" fill="#e9a04a"/><circle cx="86" cy="82" r="1" fill="#e9a04a"/>`);
D('gym','At the gym',X=>
  X.shadow(50,92,40)+
  `<path d="M66 45 Q77 32 67 22" stroke="${'#e0445c'}" stroke-width="8" stroke-linecap="round" fill="none"/>`+
  X.clay('circle','cx="73" cy="32" r="6.5"','#ff9a88','#d93a52')+
  X.clay('circle','cx="66" cy="20" r="5"','#ff9a88','#d93a52')+
  X.clay('rect','x="4" y="44" width="10" height="26" rx="5"','#7a74b8','#231f48')+X.clay('rect','x="86" y="44" width="10" height="26" rx="5"','#7a74b8','#231f48')+
  X.clay('circle','cx="19" cy="57" r="17"','#8c86cc','#27234f')+X.clay('circle','cx="81" cy="57" r="17"','#8c86cc','#27234f')+
  X.flat('circle','cx="19" cy="57" r="6"','#1d1a3a')+X.flat('circle','cx="81" cy="57" r="6"','#1d1a3a')+
  X.gloss(13,48,4,2.5)+X.gloss(75,48,4,2.5)+
  X.clay('rect','x="27" y="38" width="46" height="38" rx="17"','#ff9a88','#d4304c')+
  X.flat('rect','x="28" y="44" width="44" height="6"','#fff')+X.flat('rect','x="28" y="47" width="44" height="1.4"','#ff4d6d')+
  X.gloss(38,56,6,3,-20,.55)+
  X.eye(41,58,4.8,{look:[0,0]})+X.eye(59,58,4.8)+X.brows(41,59,51.5,4.2,-18)+
  X.mouth('open',50,66,5)+X.blush(33,67,65,4)+
  X.drop(30,30,4)+X.drop(84,24,3.2)+X.drop(24,20,2.4));
D('studying','Studying',X=>
  X.shadow(50,94,28)+
  X.flat('rect','x="66" y="22" width="12" height="62" rx="4"','#fff4dc')+
  `<path d="M69 30 H76 M69 40 H76 M69 50 H76 M69 60 H76 M69 70 H76" stroke="#e3cfa6" stroke-width="1.2"/>`+
  X.clay('rect','x="22" y="16" width="50" height="72" rx="8"','#ff7a7a','#a71f3c')+
  X.flat('rect','x="22" y="16" width="9" height="72" rx="4"','#8a1830')+
  X.flat('rect','x="38" y="24" width="26" height="6" rx="3"','rgba(255,255,255,.35)')+
  X.gloss(36,22,8,3,-8,.55)+
  X.eye(42,50,4.8,{lid:.5})+X.eye(60,50,4.8,{lid:.5})+
  `<path d="M37 57 Q42 60 47 57 M55 57 Q60 60 65 57" stroke="#6b1a52" stroke-width="1.6" stroke-linecap="round" fill="none" opacity=".75"/>`+
  `<g stroke="#1c1330" stroke-width="2.4" fill="rgba(255,255,255,.18)"><circle cx="42" cy="50" r="9"/><circle cx="60" cy="50" r="9"/></g><path d="M51 49 Q51 46 51 49" stroke="#1c1330" stroke-width="2.4"/><path d="M50.5 49 H51.5" stroke="#1c1330" stroke-width="2.4"/>`+
  X.gloss(39,46,2.5,1.2,-30,.8)+X.gloss(57,46,2.5,1.2,-30,.8)+
  X.mouth('wavy',51,68,4.5)+
  X.drop(70,12,3));
D('work','At work',X=>
  X.shadow(50,92,40)+
  X.clay('rect','x="20" y="14" width="58" height="46" rx="7"','#eef1f8','#8a91a8')+
  X.flat('rect','x="25" y="19" width="48" height="36" rx="4"',X.lg('#34468a','#141a38'))+
  X.gloss(28,22,5,2,-20,.45)+
  X.eye(40,35,5.2,{look:[-.4,0],color:'#bfe9ff'})+X.eye(58,35,5.2,{look:[-.4,0],color:'#bfe9ff'})+
  `<path d="M44 47 l2.5 -2 l2.5 2 l2.5 -2 l2.5 2" stroke="#bfe9ff" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" fill="none"/>`+
  X.clay('path','d="M10 62 H88 L82 74 H16 Z"','#e6e9f2','#7e859c')+
  X.flat('rect','x="40" y="64" width="18" height="3" rx="1.5"','#9aa1b7')+
  X.drop(76,18,3.2)+
  `<path d="M16 10 l4 5 M24 6 l1 6 M10 18 l6 3" stroke="#ff8aa8" stroke-width="2.2" stroke-linecap="round"/>`+
  `<path d="M83 44 q-3 -5 0 -9 M89 44 q-3 -5 0 -9" stroke="#fff" stroke-width="1.6" stroke-linecap="round" fill="none" opacity=".6"/>`+
  X.clay('rect','x="76" y="46" width="17" height="20" rx="4"','#ffffff','#b9bfd6')+
  X.flat('rect','x="76" y="52" width="17" height="7"','#b4643a')+
  X.clay('circle','cx="76" cy="60" r="4"','#eef1f8','#8a91a8'));
})();
