    function[r] = rect (k,n1,n2)
        n = n1:n2;
        r = (12*k-3.5 <= n).*(n <= 12*k+3.5);
    end    