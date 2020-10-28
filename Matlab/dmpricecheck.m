%Useful for cost.yml conversion from Essentials to Dynamic Market
clc 
clear

quantity_scaler = 25000;
tax = 2;

while 1
   price = input('What is sell price? ');
   
   if price <= 0
        fprintf('STOP\n');
      break; 
   else

   cost = floor(whatcost(price,quantity_scaler,tax));
   
   fprintf('Cost: %d\n',cost);
   
   end
end






%This function calculates the total cost value
%for a given selling point
%Provided calculations from Dynamic Market

function cost = whatcost(sellPrice,quantity_scaler,tax)
    cost=(exp((sellPrice/quantity_scaler)*tax)-1) * 100000;
    
    if cost > 100000
        cost = -1;
    end
end