clear
close all
clc 

% Always begin your matlab scripts with the commands above
  A = 9;
  T0 = 12;
  f0 = 1/T0;
  fs = 200;
  t = [0:T0/fs:4*T0];
  ft = zeros();
  for n = 1:10
    ft = ft + cos(2*pi*(2*n-1)*f0*t)/((2*n-1)^2);
  end
  ft = ft * 8*A/(pi^2); 


  figure('name', 'UE1 3b');
  subplot(211); hold on; grid on;
  title('Erste 10 Harmonische');
  plot(t, ft);
  xlabel('t');
  ylabel('x(t)');

  ft = 0;
  for k = 1:1000
    ft = ft + cos(2*pi*(2*k-1)*f0*t)/((2*k-1)^2);
  end
  ft = ft * 8*A/(pi^2);
  subplot(212); hold on; grid on;
  title('Erste 1000 Harmonische');
  plot(t, ft);
  xlabel('t');
  ylabel('x(t)');

  print('1_3.eps')
  
