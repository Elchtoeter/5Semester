clear
close all
clc 

mz=0:0.01:0.9
sz=0:0.01:0.7
t=0:(1/12):1
t1=0:(1/60):1
t2=0:0.01:1

totalmins = 545

x1 = 1*exp(j*2*pi*(-t+0.25));
stundenMarker = 1.25*exp(j*2*pi*(-t+0.25-(1/12)));
x3 = 1*exp(j*2*pi*(-t1+0.25));
x4 = 1.1*exp(j*2*pi*(-t2+0.25));

h=figure

for k = 1:20
  clf
  hold on;axis equal;axis([-1.5 1.5 -1.5 1.5])
  axis off
  center = 0+j*0
  totalmins = totalmins + 55;
  mins = mod(totalmins,60)
  std = (totalmins/60)
  minutenZeiger = mz*exp(j*2*pi*(0.25-(mins/60)));
  stundenZeiger = sz*exp(j*2*pi*(0.25-(std/12)));
  minutenKnoten = 0.9*exp(j*2*pi*(0.25-(mins/60)));
for l = 1:12
  text(real(stundenMarker(l)),imag(stundenMarker(l)),num2str(l),'FontSize',24,'Color','magenta','HorizontalAlignment','center')
endfor
plot(x1,'m>',"markersize",10)
plot(x3,'bx');
plot(x4);
plot(0,0,'ko','MarkerSize',12,'MarkerFaceColor','k');
%wenn man nur die pure komplexe Nummer plottet geht es zwar auch aber es kommt
%zu Problemen wenn der komplexe Teil zu 0 evaluiert
plot(real(minutenZeiger),imag(minutenZeiger),'LineWidth',2.0,'Color','k');
plot(real(stundenZeiger),imag(stundenZeiger),'LineWidth',4.0,'Color','k');
plot(real(minutenKnoten),imag(minutenKnoten),'ko','markersize',10,'MarkerFaceColor','k');
pause(0.01);

endfor

