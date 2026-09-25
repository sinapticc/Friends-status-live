(function(){
if (window.FSL) return;
const F = window.FSL = { reg:{}, pending:new Set(), n:0, list:[] };
const INK = '#1c1330';
function mk(u){
  const d=[]; let i=0;
  const X = { u,
    g(c1,c2,cx=.34,cy=.28){const id=u+'g'+(i++);d.push(`<radialGradient id="${id}" cx="${cx}" cy="${cy}" r=".95"><stop offset="0" stop-color="${c1}"/><stop offset="1" stop-color="${c2}"/></radialGradient>`);return `url(#${id})`;},
    lg(c1,c2,x2=0,y2=1){const id=u+'g'+(i++);d.push(`<linearGradient id="${id}" x1="0" y1="0" x2="${x2}" y2="${y2}"><stop offset="0" stop-color="${c1}"/><stop offset="1" stop-color="${c2}"/></linearGradient>`);return `url(#${id})`;},
    clay(tag,attrs,c1,c2,cx,cy){return `<${tag} ${attrs} fill="${X.g(c1,c2,cx,cy)}" filter="url(#${u}c)"/>`;},
    flat(tag,attrs,fill){return `<${tag} ${attrs} fill="${fill}"/>`;},
    gloss(cx,cy,rx,ry,rot=-25,o=.75){return `<ellipse cx="${cx}" cy="${cy}" rx="${rx}" ry="${ry}" fill="#fff" opacity="${o}" filter="url(#${u}b)" transform="rotate(${rot} ${cx} ${cy})"/>`;},
    shadow(cx=50,cy=93,rx=30,o=.32){return `<ellipse cx="${cx}" cy="${cy}" rx="${rx}" ry="${rx*.16}" fill="#000" opacity="${o}" filter="url(#${u}s)"/>`;},
    eye(x,y,r,o={}){
      const k=o.kind||'open', c=o.color||INK;
      if(k==='happy') return `<path d="M${x-r} ${y+r*.3} Q${x} ${y-r*1.2} ${x+r} ${y+r*.3}" stroke="${c}" stroke-width="${r*.5}" stroke-linecap="round" fill="none"/>`;
      if(k==='closed') return `<path d="M${x-r} ${y-r*.2} Q${x} ${y+r*1.1} ${x+r} ${y-r*.2}" stroke="${c}" stroke-width="${r*.45}" stroke-linecap="round" fill="none"/>`;
      if(k==='line') return `<path d="M${x-r} ${y} H${x+r}" stroke="${c}" stroke-width="${r*.45}" stroke-linecap="round"/>`;
      if(k==='heart') return X.heart(x,y,r*1.15,'#ff2f6d')+`<circle cx="${x-r*.4}" cy="${y-r*.3}" r="${r*.22}" fill="#fff"/>`;
      if(k==='star') return X.spark(x,y,r*1.2,c)+`<circle cx="${x-r*.25}" cy="${y-r*.25}" r="${r*.15}" fill="#fff"/>`;
      if(k==='spiral') return `<path d="M${x} ${y} m${-r*.2} 0 a${r*.2} ${r*.2} 0 1 1 ${r*.4} 0 a${r*.45} ${r*.45} 0 1 1 ${-r*.9} 0 a${r*.7} ${r*.7} 0 1 1 ${r*1.4} 0 a${r*.95} ${r*.95} 0 1 1 ${-r*1.9} 0" stroke="${c}" stroke-width="${r*.26}" stroke-linecap="round" fill="none"/>`;
      if(k==='squeeze') return `<path d="M${x-r*.8} ${y-r*.7} L${x+r*.6} ${y} L${x-r*.8} ${y+r*.7}" stroke="${c}" stroke-width="${r*.42}" stroke-linecap="round" stroke-linejoin="round" fill="none" transform="${o.flip?`scale(-1 1) translate(${-2*x} 0)`:''}"/>`;
      const lx=(o.look||[0,0])[0]*r*.22, ly=(o.look||[0,0])[1]*r*.22;
      const lid=o.lid||0; const cid=u+'e'+(i++);
      let s=`<ellipse cx="${x+lx}" cy="${y+ly}" rx="${r*.86}" ry="${r}" fill="${c}"/>`+
        `<circle cx="${x+lx-r*.3}" cy="${y+ly-r*.38}" r="${r*.3}" fill="#fff"/>`+
        `<circle cx="${x+lx+r*.3}" cy="${y+ly+r*.34}" r="${r*.13}" fill="#fff" opacity=".85"/>`;
      if(lid){const cut=y-r*1.1+r*2.2*lid; d.push(`<clipPath id="${cid}"><rect x="${x-r*2}" y="${cut}" width="${r*4}" height="${r*3}"/></clipPath>`);
        s=`<g clip-path="url(#${cid})">${s}</g><path d="M${x-r*1.05} ${cut+r*.12} Q${x} ${cut-r*.22} ${x+r*1.05} ${cut+r*.12}" stroke="${c}" stroke-width="${r*.34}" stroke-linecap="round" fill="none"/>`;}
      return s;
    },
    eyes(x1,x2,y,r,o){return X.eye(x1,y,r,o)+X.eye(x2,y,r,o);},
    brows(x1,x2,y,w,ang,c=INK){const b=(x,s)=>{const dy=Math.tan(ang*Math.PI/180)*w*s;return `<path d="M${x-w} ${y-dy} L${x+w} ${y+dy}" stroke="${c}" stroke-width="${w*.45}" stroke-linecap="round"/>`;};return b(x1,1)+b(x2,-1);},
    blush(x1,x2,y,r=5,o=.55){return [x1,x2].map(x=>`<ellipse cx="${x}" cy="${y}" rx="${r}" ry="${r*.6}" fill="#ff6f9c" opacity="${o}" filter="url(#${u}b)"/>`).join('');},
    mouth(t,x,y,s){const st=`stroke="${INK}" stroke-width="${s*.42}" stroke-linecap="round" stroke-linejoin="round" fill="none"`;
      if(t==='smile') return `<path d="M${x-s} ${y} Q${x} ${y+s*1.1} ${x+s} ${y}" ${st}/>`;
      if(t==='smirk') return `<path d="M${x-s} ${y+s*.25} Q${x+s*.1} ${y+s*.75} ${x+s} ${y-s*.35}" ${st}/>`;
      if(t==='frown') return `<path d="M${x-s} ${y+s*.45} Q${x} ${y-s*.45} ${x+s} ${y+s*.45}" ${st}/>`;
      if(t==='flat') return `<path d="M${x-s*.8} ${y} H${x+s*.8}" ${st}/>`;
      if(t==='zig') return `<path d="M${x-s} ${y} l${s*.5} ${-s*.4} l${s*.5} ${s*.4} l${s*.5} ${-s*.4} l${s*.5} ${s*.4}" ${st}/>`;
      if(t==='wavy') return `<path d="M${x-s} ${y} q${s*.5} ${-s*.5} ${s} 0 t${s} 0" ${st}/>`;
      if(t==='o') return `<ellipse cx="${x}" cy="${y}" rx="${s*.45}" ry="${s*.55}" fill="${INK}"/>`;
      const id=u+'m'+(i++);
      d.push(`<clipPath id="${id}"><path d="M${x-s} ${y} Q${x} ${y+s*1.9} ${x+s} ${y} Z"/></clipPath>`);
      return `<path d="M${x-s} ${y} Q${x} ${y+s*1.9} ${x+s} ${y} Z" fill="#3b1230" stroke="#3b1230" stroke-width="${s*.2}" stroke-linejoin="round"/><ellipse clip-path="url(#${id})" cx="${x}" cy="${y+s*1.05}" rx="${s*.6}" ry="${s*.4}" fill="#ff6f8e"/>`;
    },
    drop(x,y,s,c1='#e6f7ff',c2='#4fb3ff'){return `<path d="M${x} ${y-s} C${x+s*.9} ${y+s*.1} ${x+s*.7} ${y+s} ${x} ${y+s} C${x-s*.7} ${y+s} ${x-s*.9} ${y+s*.1} ${x} ${y-s} Z" fill="${X.g(c1,c2,.35,.5)}"/><circle cx="${x-s*.25}" cy="${y+s*.2}" r="${s*.18}" fill="#fff" opacity=".9"/>`;},
    spark(x,y,s,c='#fff'){return `<path d="M${x} ${y-s} Q${x+s*.15} ${y-s*.15} ${x+s} ${y} Q${x+s*.15} ${y+s*.15} ${x} ${y+s} Q${x-s*.15} ${y+s*.15} ${x-s} ${y} Q${x-s*.15} ${y-s*.15} ${x} ${y-s} Z" fill="${c}"/>`;},
    heart(x,y,s,c='#ff4d7d'){return `<path d="M${x} ${y+s*.95} C${x-s*1.5} ${y-s*.05} ${x-s*.95} ${y-s*1.15} ${x} ${y-s*.35} C${x+s*.95} ${y-s*1.15} ${x+s*1.5} ${y-s*.05} ${x} ${y+s*.95} Z" fill="${c}"/>`;},
    z(x,y,s,c='#d9d2ff'){return `<path d="M${x} ${y} h${s} l${-s} ${s} h${s}" stroke="${c}" stroke-width="${s*.32}" stroke-linecap="round" stroke-linejoin="round" fill="none"/>`;},
    defs(){return d.join('');}
  };
  return X;
}
F.svg=function(key){
  const fn=F.reg[key]; if(!fn) return '';
  const u='fs'+(F.n++); const X=mk(u); X.o=arguments[1]||{}; X.v=X.o.v||''; X.key=key; if(F.hook) F.hook(X); let body=fn(X,X.o);
  const hv=(F.hue[key]||[0,0,0])[['boy','girl','neutral'].indexOf(X.v)]||0, past=X.v==='girl';
  let hf=''; if(hv||past){ const t=past?'<feComponentTransfer><feFuncR type="linear" slope=".9" intercept=".1"/><feFuncG type="linear" slope=".9" intercept=".1"/><feFuncB type="linear" slope=".9" intercept=".1"/></feComponentTransfer>':''; hf=`<filter id="${u}h" color-interpolation-filters="sRGB"><feColorMatrix type="hueRotate" values="${hv}"/>${t}</filter>`; body=`<g filter="url(#${u}h)">${body}</g>`; }
  if(F.post) body+=F.post(X,key);
  return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="-6 -6 112 112" width="224" height="224"><defs>`+
  `<filter id="${u}c" x="-15%" y="-15%" width="130%" height="130%"><feGaussianBlur in="SourceAlpha" stdDeviation="3.2"/><feOffset dx="-3" dy="-4" result="o"/><feComposite in="SourceAlpha" in2="o" operator="out" result="e"/><feFlood flood-color="#1a0b2e" flood-opacity=".38"/><feComposite in2="e" operator="in" result="sh"/><feGaussianBlur in="SourceAlpha" stdDeviation="1.8"/><feOffset dx="1.6" dy="2.4" result="o2"/><feComposite in="SourceAlpha" in2="o2" operator="out" result="e2"/><feFlood flood-color="#fff" flood-opacity=".5"/><feComposite in2="e2" operator="in" result="hl"/><feMerge><feMergeNode in="SourceGraphic"/><feMergeNode in="sh"/><feMergeNode in="hl"/></feMerge></filter>`+
  `<filter id="${u}b" x="-50%" y="-50%" width="200%" height="200%"><feGaussianBlur stdDeviation="1.4"/></filter>`+
  `<filter id="${u}s" x="-50%" y="-200%" width="200%" height="500%"><feGaussianBlur stdDeviation="2.6"/></filter>`+
  X.defs()+hf+`</defs>${body}</svg>`;
};
F.cache={};F.url=function(key,o){ const ck=key+'|'+JSON.stringify(o||{}); if(F.cache[ck]) return F.cache[ck]; const s=F.svg(key,o); if(!s) return ''; return F.cache[ck]='data:image/svg+xml;charset=utf-8,'+encodeURIComponent(s); };
F.mix=function(a,b,t){const p=h=>{h=h.replace('#','');if(h.length===3)h=h.split('').map(c=>c+c).join('');return [0,2,4].map(i=>parseInt(h.substr(i,2),16));};const A=p(a),B=p(b);return '#'+A.map((v,i)=>Math.round(v+(B[i]-v)*t).toString(16).padStart(2,'0')).join('');};
F.anch={};F.hue={};F.accReg={};F.defAcc=function(k,fn){F.accReg[k]=fn;F.reg['__acc_'+k]=fn;};
F.def=function(key,label,fn){F.reg[key]=fn; if(!F.list.find(c=>c.key===key)) F.list.push({key,label}); F.pending.forEach(el=>{if(el.getAttribute('c')===key){F.pending.delete(el);el.render();}});};
class FslChar extends HTMLElement{
  static get observedAttributes(){return ['c','tint','face','acc','outfit','hue','v'];}
  opts(){ const o={}; ['tint','face','acc','outfit','v'].forEach(k=>{const v=this.getAttribute(k); if(v) o[k]=v;}); return o; }
  connectedCallback(){ if(!this.style.display) this.style.display='inline-block'; this.render(); this.idle(); }
  disconnectedCallback(){ this._idle&&this._idle.cancel(); this._idle=null; }
  attributeChangedCallback(n,o,v){ if(!this.isConnected) return; this.render(); if(o&&o!==v) this.bounce(); }
  render(){ const c=this.getAttribute('c'); const o=this.opts(); const s=F.url(c,c==='me'?o:(o.v?{v:o.v}:{})); if(!s){F.pending.add(this);return;} let w=this._w; if(!w||w.parentNode!==this){ w=document.createElement('span'); w.setAttribute('data-fsl',''); w.style.cssText='display:block;width:100%;height:100%;transform-origin:50% 90%'; this.appendChild(w); this._w=w; } const hue=this.getAttribute('hue'); let h='<img alt="" draggable="false" style="display:block;width:112%;height:112%;margin:-6%;max-width:none'+(hue&&hue!=='0'?';filter:hue-rotate('+hue+'deg)':'')+'" src="'+s+'">';
    if(c!=='me'&&o.acc&&o.acc!=='none'&&F.accReg[o.acc]){ const a=F.anch[c]||[50,14,1]; const au=F.url('__acc_'+o.acc); if(au) h+='<img alt="" style="position:absolute;left:'+(a[0]-28*a[2])+'%;top:'+(a[1]-28*a[2])+'%;width:'+(56*a[2])+'%;height:'+(56*a[2])+'%;max-width:none" src="'+au+'">'; }
    w.style.position='relative'; w.innerHTML=h; this.idle(true); }
  idle(re){ if(!this.hasAttribute('idle')||!this._w) return; if(this._idle&&!re) return; this._idle&&this._idle.cancel();
    this._idle=this._w.animate([{transform:'translateY(0) rotate(-2deg)'},{transform:'translateY(-5%) rotate(2deg)'}],{duration:1400+Math.random()*600,iterations:Infinity,direction:'alternate',easing:'ease-in-out',delay:-Math.random()*1400}); }
  bounce(){ this.animate([{transform:'scale(1)'},{transform:'scale(.72,1.22) translateY(-10%)',offset:.28},{transform:'scale(1.16,.86)',offset:.58},{transform:'scale(.96,1.04)',offset:.8},{transform:'scale(1)'}],{duration:640,easing:'cubic-bezier(.3,1.4,.5,1)'}); }
}
customElements.define('fsl-char',FslChar);
class FslLive extends HTMLElement{
  connectedCallback(){ const c=this.getAttribute('color')||'currentColor'; if(!this.style.color) this.style.color='#c8f542'; const s=parseFloat(this.getAttribute('size')||8);
    this.style.cssText+=`;position:relative;display:inline-block;width:${s}px;height:${s}px;flex:none`;
    if(this._a) return; const a=document.createElement('span'),b=document.createElement('span'); a.style.cssText=b.style.cssText=`position:absolute;inset:0;border-radius:50%;background:${c}`; this.append(a,b); this._a=a;
    b.animate([{transform:'scale(1)',opacity:.7},{transform:'scale(2.8)',opacity:0}],{duration:1600,iterations:Infinity,easing:'cubic-bezier(.2,.6,.4,1)'});
    a.animate([{transform:'scale(1)'},{transform:'scale(.8)'},{transform:'scale(1)'}],{duration:1600,iterations:Infinity}); }
}
customElements.define('fsl-live',FslLive);
})();
