clear;
close all;
clc;

omega = -4*pi:8*pi/4001:4*pi;
x1 = 1./(1-(0.8*exp(-j*omega)))

subplot(411);
plot(omega, abs(x1));
xticks([-4*pi -3*pi -2*pi -pi 0 pi 2*pi 3*pi 4*pi])
ylabel ("Absolutbetrag von X");
xlabel ("w[rad]");


subplot(412);
plot(omega, angle(x1));
xticks([-4*pi -3*pi -2*pi -pi 0 pi 2*pi 3*pi 4*pi])
ylabel ("Phase von X");
xlabel ("w[rad]");



subplot(413);
plot(omega, imag(x1));
xticks([-4*pi -3*pi -2*pi -pi 0 pi 2*pi 3*pi 4*pi])
ylabel ("Imaginärteil von X");
xlabel ("w[rad]");


subplot(414);
plot(omega, real(x1);
xticks([-4*pi -3*pi -2*pi -pi 0 pi 2*pi 3*pi 4*pi])
ylabel ("Realteil von X");
xlabel ("w[rad]");