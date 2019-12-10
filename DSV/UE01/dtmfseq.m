function [sig_out] = dtmfseq(v,fs)
% fs ... [1x1] sampling frequency in [Hz]
% v .... [nx1] or [1xn] vector of information symbols, 
%              e.g. telephone number v=[0 8 1 5 1 2 3 4 5 6 7 8]
% 

Ts = ???;                       % Sampling time [s]
T_tone = ???;                   % Symbol duration [s]

t_vec = ???;                    % Time sample vector for generation of one tone symbol

T_silence = ???;                % Duration of silence sequence between symbols [s] (e.g. zeros(), ones())
N_sil = round(T_silence/Ts);    % Number of samples of silence sequence
silence_sig = ???;              % Pause/silence sequence

sig_out = [];
for k = 1:numel(v)
    
    switch v(k)
        case 0
            % choose corresponding frequencies of DTMF symbol
            f1 = ???;           % [Hz]
            f2 = ???;           % [Hz]
        case 1
            f1 = ???;           % [Hz]
            f2 = ???;           % [Hz]
        ???
    end
        
    tone = sin(2*pi*f1*t_vec) + sin(2*pi*f2*t_vec);   % generation of one tone
    
    sig_out = [sig_out tone silence_sig];   % appending one tone + pause to tone sequence
end