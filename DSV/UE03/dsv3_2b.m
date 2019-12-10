clear;
close all;
clc;

w = -4*pi:8*pi/4000:4*pi;
% (0.8)^|n| (u[n + 10] − u[n − 11])

n = -10:10

x2 = (0.8).^abs(n)

X = dtft(x2,n,w)

figure
subplot(211);
plot(w, real(X));
xticks([-4*pi -3*pi -2*pi -pi 0 pi 2*pi 3*pi 4*pi])
ylabel ("Realteil von X");
xlabel ("w[rad]");

subplot(212);
plot(w, angle(X));
xticks([-4*pi -3*pi -2*pi -pi 0 pi 2*pi 3*pi 4*pi])
ylim([-90 90]);
ylabel ("Phase von X");
xlabel ("w[rad]");