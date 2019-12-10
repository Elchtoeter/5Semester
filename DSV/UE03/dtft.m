function X = dtft(x,n,w)
%DTFT Computes Discrete-time Fourier transform
%   @param  x: finite duration sequence over n
%   @param  n: sample position vector
%   @param  w: freqeuncy location vector
%   @return X: DTFT values computed at w frequencies
 
 
    for k = 1:1:length(w)
        s = x.*exp(-j*w(k)*n);
        X(k) = sum(s);
    end
end