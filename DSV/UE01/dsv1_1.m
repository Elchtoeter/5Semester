clear
close all
clc 

% Always begin your matlab scripts with the commoands above
%% Discrete-time sinusoidal example
	M = 12;             % Number of samples in discrete time vector
	n1 = -5:10;            % Discrete time vector
	n2 = -5:10;
	n3 = 0:256;
	n4 = 0:256;
    n5 = -5:20;
    n6 = -17:17;
    
% Signale
	  x1 = 3.*impseq(-2,-5,10)+ 2.*impseq(1,-5,10)-impseq(4,-5,10)+4.*impseq(7,-5,10);
    x2 = exp(-0.3*n2);
	  x3 = 2*sin(((2*pi)/64)*n3);
	  x4 = cos((9/64)*n4);
    x5 = 5.*stepseq(0,-5,20)-6.*stepseq(5,-5,20)-3.*stepseq(10,-5,20)+5.*stepseq(15,-5,20);
    x6 = rect(-1,-17,17)+rect(0,-17,17)+rect(1,-17,17);
    x7 = randn(10,1);

	% Visualization
	% Name
	figure('name', 'Aufgabe1');
    
	subplot(321); hold on; grid on;
	% Name des Subplots
	title('X_1');
	stem(n1, x1);
	xlabel('n');
	ylabel('x_1[n]');
  
  
	subplot(322); hold on; grid on;
	% Name des Subplots
	title('X_2');
	stem(n2, x2);
	xlabel('n');
	ylabel('x_2[n]');
  

	subplot(323); hold on; grid on;
	% Name des Subplots
	title('X_3');
	plot(n3, x3);
	xlabel('n');
	ylabel('x_3[n]');
 

	subplot(324); hold on; grid on;
	% Name des Subplots
	title('X_4');
	plot(n4, x4);
	xlabel('n');
	ylabel('x_4[n]');
  
    
    subplot(325); hold on; grid on;
	% Name des Subplots
	title('X_5');
	stem(n5, x5);
	xlabel('n');
	ylabel('x_5[n]');

    
    subplot(326); hold on; grid on;
	% Name des Subplots
	title('X_6');
	plot(n6, x6);
	xlabel('n');
	ylabel('x_6[n]');
  print ("x1_6.eps")
    
    energien = [energie(x1), energie(x6), energie(x7)]
    leistungen = [leistung(x3), leistung(x6)]
       

  

