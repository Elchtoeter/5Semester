clear
close all
clc 

%Always begin your matlab scripts with the commoands above

%% Discrete-time sinusoidal example
M = 15;             % Number of samples in discrete time vector
n = 0:M;            % Discrete time vector
x1 = sin(0.5*n);
x2 = cos(pi/16*n);

% Visualization
figure;
subplot(211); hold on; grid on;
stem(n, x1);
xlabel('n');
ylabel('x_1[n]');

subplot(212); hold on; grid on;
stem(n, x2);
xlabel('n');
ylabel('x_2[n]');



