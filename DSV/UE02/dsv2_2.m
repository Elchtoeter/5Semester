clear
close all
clc

f0 = 3000;
T0 = 1/f0;
fs = 1/(f0*100);
t  = 0:fs:T0*2;
x1 = -3;
x2 = 2*sin(20*pi*f0*t);
x3 = -2*sin(4*pi*f0*t);
x4 = -3*cos(2*pi*f0*t);
xt = x1+x2+x3+x4;

% Graphische Ausgabe
  figure('name', 'UE2 2a');
  subplot(511); hold on; grid on;
  title('Gleichanteil');
  plot(t,x1);
  xlabel('t[ms]');
  ylabel('x(t)');

  subplot(512); hold on; grid on;
  title('1. Harmonische');
  plot(t, x4);
  xlabel('t[ms]');
  ylabel('x(t)');

  subplot(513); hold on; grid on;
  title('2. Harmonische');
  plot(t, x3);
  xlabel('t[ms]');
  ylabel('x(t)');

  subplot(514); hold on; grid on;
  title('10. Harmonische');
  plot(t, x2);
  xlabel('t[ms]');
  ylabel('x(t)');

  subplot(515); hold on; grid on;
  title('Summe');
  plot(t, xt);
  xlabel('t[ms]');
  ylabel('x(t)');

  print('fig\Aufgabe2_2_a.png')


  % 2b) fourier koeffizienten, ak, bk
  a0 = -6
  b10 = 2
  b2 = -2
  a1 = -3

  b1 = 0;
  a2 = 0;
  a10 = 0;


  
  c0 = a0/2;
  % ck = 1/2*(ak-i*bk);
  % c-k = 1/2*(ak+i*bk);
  c1 = 1/2*(a1-i*b1)
  c2 = 1/2*(a2-i*b2)
  c10 = 1/2*(a10-i*b10)
  c11 = 1/2*(a1+i*b1)
  c21 = 1/2*(a2+i*b2)
  c101 = 1/2*(a10+i*b10)

  xfr=c0*exp(0)+c1*exp(i*2*pi*f0*t)+c2*exp(i*4*pi*f0*t)+c10*exp(i*20*pi*f0*t)+c11*exp(i*-2*pi*f0*t)+c21*exp(i*-4*pi*f0*t)+c101*exp(i*-20*pi*f0*t);
  xt=x1+x2+x3+x4;
  figure('name', 'UE2 2c');
  subplot(211); hold on; grid on;
  title('xt');
  plot(t, xt);
  xlabel('t[ms]');
  ylabel('x(t)');

  subplot(212); hold on; grid on;
  title('Realteil der komplexen Fourier-Reihe');
  plot(t, real(xfr));
  xlabel('t[ms]');
  ylabel('x(t)');
  print('fig\Aufgabe2_2_c.png')
  
