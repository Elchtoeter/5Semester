 function [l] = leistung (signal)
        l = sum(signal.^2)/length(signal);
    end