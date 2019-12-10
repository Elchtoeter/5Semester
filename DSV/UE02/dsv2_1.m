clear
close all
clc 

%% Aufgabe 1
	c1 = -3+5*1i;
	c2 = sqrt(2)*exp(-1i*3*pi/4);
	c3 = 1/sqrt(2) + 1i/sqrt(2);
	c4 = 1 + 1i*3;
	
	c5 = c1+c2;
	c6 = c1-c2;
	c7 = c1*c2;
	c8 = abs(c2);
	c9 = abs(c3^2);
	c10 = atan(c4);
	c11 = c1/c2;
	
	complex = [c5,c6,c7,c8];
	reell = real(complex);
	imaginaer = imag(complex);
	
	n = 0:100;
	x = (0.9 * exp(1i*pi/10)).^n;
	
% Visualization
	figure('name', 'Aufgabe_1b'); hold on; grid on;	
  axis([-5 9 -4 8])
	plot(c6,'ko');
	plot(c7,'ro');
	plot(c8,'bo');
	plot(c9,'mo');
	ylabel('Imaginärteil[c]');
	xlabel('Realteil[c]');
	legend('c5','c6','c7','c8');
  
  print('fig\Aufgabe2_1_b.png')
    	
	figure('name','1c'); hold on; grid on;	
	subplot(411); hold on; grid on;
	plot(n, abs(x));
	xlabel('n');
	ylabel('abs[n]');
	
	subplot(412); hold on; grid on;
	plot(n, angle(x));
	xlabel('n');
	ylabel('angle[n]');
	
	subplot(413); hold on; grid on;
	plot(n, real(x));
	xlabel('n');
	ylabel('Re[n]');
	
	subplot(414); hold on; grid on;
	plot(n, imag(x));
	xlabel('n');
	ylabel('Im[n]');
	
  print('fig\Aufgabe2_1_c.png')
  
% 1d)	
	a1=45;
	a2=-90;
	a3=pi/4;
	a4=7*pi/3;
    
	disp(deg2rad(a1));
	disp(deg2rad(a2));
	disp(deg2rad(a3));
	disp(rad2deg(a4));