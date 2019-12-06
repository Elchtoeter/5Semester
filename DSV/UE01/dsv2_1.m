clear
close all
clc 

% Always begin your matlab scripts with the commands above
  A = 9;
  T0 = 12;
  f0 = 1/T0;
  fs = 200;
  t = [0:T0/fs:4*T0];
  xt = zeros(1, numel(t));
  for k = 1:9
    xt = xt + cos(2*pi*(2*k-1)*f0*t)/((2*k-1)^2);
  end
  xt = xt * 8*A/(pi^2); 
  
% Zum Erkennen der Rundung
  % xt

% Graphische Ausgabe
  figure('name', 'UE2 1b');
  subplot(211); hold on; grid on;
  title('Summe der ersten 10 Harmonische Fourie-Reihe');
  plot(t, xt);
  xlabel('t/ms');
  ylabel('x(t)');

  xt = 0;
  for k = 1:999
    xt = xt + cos(2*pi*(2*k-1)*f0*t)/((2*k-1)^2);
  end
  xt = xt * 8*A/(pi^2);
  subplot(212); hold on; grid on;
  title('Summe der ersten 1000 Harmonische Fourie-Reihe');
  plot(t, xt);
  xlabel('t/ms');
  ylabel('x(t)');

  print('latex/fig/Aufgabe2_1_b.png')
  
% Zum Erkennen der Rundung
  % xt
