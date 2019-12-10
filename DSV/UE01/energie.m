function [e] = energie (signal)
        e = sum(signal.^2);
    end