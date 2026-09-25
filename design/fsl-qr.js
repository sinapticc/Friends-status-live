(function(){ if(customElements.get('fsl-qr')) return;
class FslQr extends HTMLElement{
  static get observedAttributes(){return ['data','color'];}
  connectedCallback(){this.render();} attributeChangedCallback(){ if(this.isConnected) this.render(); }
  render(){ const d=this.getAttribute('data')||'fsl', c=this.getAttribute('color')||'#1c1330', N=25;
    let h=2166136261; for(const ch of d){h^=ch.charCodeAt(0);h=Math.imul(h,16777619);} const rnd=()=>{h^=h<<13;h^=h>>>17;h^=h<<5;return ((h>>>0)%1000)/1000;};
    const fin=(x,y)=>(x<8&&y<8)||(x>=N-8&&y<8)||(x<8&&y>=N-8);
    let r=''; for(let y=0;y<N;y++)for(let x=0;x<N;x++){ if(fin(x,y)) continue; if(x>=10&&x<=14&&y>=10&&y<=14) continue; if(rnd()<.5) r+=`<rect x="${x}" y="${y}" width="1" height="1" rx=".32"/>`; }
    const f=(x,y)=>`<rect x="${x+.5}" y="${y+.5}" width="6" height="6" rx="1.8" fill="none" stroke="${c}" stroke-width="1"/><rect x="${x+2}" y="${y+2}" width="3" height="3" rx=".9"/>`;
    let w=this._w; if(!w||w.parentNode!==this){ w=document.createElement('span'); w.style.cssText='display:block;width:100%;height:100%'; this.appendChild(w); this._w=w; }
    w.innerHTML=`<svg viewBox="-1 -1 ${N+2} ${N+2}" width="100%" height="100%" style="display:block" fill="${c}">${r}${f(0,0)}${f(N-7,0)}${f(0,N-7)}<rect x="10.2" y="10.2" width="4.6" height="4.6" rx="1.4" fill="#c8f542"/></svg>`; }
}
customElements.define('fsl-qr',FslQr);
class FslConfetti extends HTMLElement{
  connectedCallback(){ if(this._on) return; this._on=1; const cols=['#c8f542','#ff5ca8','#4fd1ff','#ffd23a','#b49bff','#fff'];
    const box=document.createElement('span'); box.style.cssText='position:absolute;inset:0;overflow:hidden;pointer-events:none'; this.appendChild(box);
    for(let i=0;i<46;i++){ const p=document.createElement('span'); const w=5+Math.random()*7, h=Math.random()<.4?w:w*.45;
      p.style.cssText=`position:absolute;left:${Math.random()*100}%;top:-20px;width:${w}px;height:${h}px;border-radius:${Math.random()<.3?'50%':'2px'};background:${cols[i%cols.length]}`; box.appendChild(p);
      const dx=(Math.random()-.5)*120, r=(Math.random()-.5)*900;
      p.animate([{transform:'translate(0,0) rotate(0)'},{transform:`translate(${dx}px,${(this.offsetHeight||900)+40}px) rotate(${r}deg)`}],{duration:2600+Math.random()*2400,delay:Math.random()*2200,iterations:Infinity,easing:'cubic-bezier(.25,.4,.6,1)'}); } }
}
customElements.define('fsl-confetti',FslConfetti);
})();
