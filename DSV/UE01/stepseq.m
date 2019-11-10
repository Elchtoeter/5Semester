function [ x,n ] = stepseq( n0,n1,n2 )
%IMPSEQ Generates x(n) = u(n-n0), whereas n1 <= n <= n2
%   Usage: [x,n] = stepseq(n0, n1, n2);

n = n1:n2;
x = (n-n0) >= 0;


end

