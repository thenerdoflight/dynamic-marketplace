package org.dynamicmarketplace.dynamicmarketplace;

import net.milkbowl.vault.economy.Economy;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynamicmarketplace.dynamicmarketplace.savedata.*;

public final class DynamicMarketplace extends JavaPlugin {

    private Economy economy;

    private InputParser inputParser;
    private Processor processor;

    private Config config;
    private Costs costs;
    private Recipies recipies;

    // Init

    public void onEnable() {
        
        // Ecconomy
        setupEconomy();

        // Save Data
        config = new Config("plugins/DynamicMarket/CONFIG.txt");
        costs = new Costs(config.costFiles);
        recipies = new Recipies(config.recipieFiles);

        // Commands
        inputParser = new InputParser();
        processor = new Processor(recipies, costs, config);

    }

    private void setupEconomy () {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration( net.milkbowl.vault.economy.Economy.class);
        economy = economyProvider == null ? null : economyProvider.getProvider();
        if ( economy == null ){
            Interactions.ecconomyNotLoaded();
            Bukkit.shutdown();
        }
    }

    // Commands

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ( ! (sender instanceof Player) ) return true;

        Player player = (Player) sender;
        int count = 0;

        if (player.hasPermission("dynamicmarket.use")) {
        	switch ( command.getName().toLowerCase() ){

            // Item Info

            case "infohand":
                return getInfoOnHand( player );
            case "iteminfo":
                if ( args.length == 0 ) 
                    return getInfoOnHand( player );
                return getInfoOnItem( player, args[0] );
            case "cost":
                if ( args.length == 0 ) 
                    return false;
                if (args.length == 1 )
                    return doCostings(player, args[0], 1) ;
                count = inputParser.castInt(args[1], player);
                return doCostings(player, args[0], count) ;


            // Purchasing Items
            
            case "buy":
                if ( args.length == 0 ) 
                    return false;
                if ( args.length == 1 ) 
                    return purchaseItem(args[0], 1, player);
                count = inputParser.castInt(args[1], player);
                return count < 1 ? true : purchaseItem(args[0], count, player);

            case "buyhand":
                if ( args.length == 0 ) 
                    return purchaseHand( 1, player);
                count = inputParser.castInt(args[0], player);
                return count < 1 ? true : purchaseHand( count, player);

            // Sell Items

            case "sellhand":
                if ( args.length == 0 ) 
                    return sellHand( processor.getHandQuantity(player), player);
                count = inputParser.castInt(args[0], player);
                return count < 1 ? true : sellHand( count, player);
            
            case "sell":
                if ( args.length == 0 ) 
                    return false;
                if ( args.length == 1 ) 
                    return sellItem(args[0], 1, player);
                count = inputParser.castInt(args[1], player);
                return count < 1 ? true : sellItem( args[0], count, player);

            case "sellall":
                return sellAll(player);

        }
        	
        }else {
        	player.sendMessage("You do not have permssion");
        }
        
 
        return true;
    }

    // Item Info

    private boolean getInfoOnHand ( Player player ) {
        String item = processor.getHeldItem(player);
        Boolean valid = processor.isValidItem(player, item);
        if ( !valid ) return true;
        return getInfoOnItem( player, item );
    }
    private boolean getInfoOnItem ( Player player, String item ) {
        Boolean valid = processor.isValidItem(player, item);
        if ( !valid ) return true;
        Double quantity = processor.getShopQuantity(item);
        Double buySingle = processor.getBuyPrice(item, 1);
        Double buyStack = processor.getBuyPrice(item, 64);
        Double sellSingle = processor.getSalePrice(item, 1);
        Double sellStack = processor.getSalePrice(item, 64);
        Interactions.itemInfo(item, player, quantity, new double[]{buySingle, buyStack, sellSingle, sellStack});
        return true;
    }

    private boolean doCostings ( Player player, String item, int amount ){
        Boolean valid = processor.isValidItem(player, item);
        if ( !valid ) return true;
        Double buy = processor.getBuyPrice(item, amount);
        Double sell = processor.getSalePrice(item, amount);
        Interactions.costing(item, player, amount, buy, sell);
        return true;
    }

    // Purchasing Items

    private boolean purchaseHand ( int quantity, Player player) {
        String item = processor.getHeldItem(player);
        Boolean valid = processor.isValidItem(player, item);
        if ( !valid ) return true;
        return purchaseItem(item, quantity, player);
    }
    
    private boolean purchaseItem ( String item, int quantity, Player player ) {
        Boolean valid = processor.isValidItem(player, item);
        if ( !valid ) return true;
        Double cost = processor.getBuyPrice(item, quantity);
        Double balance = economy.getBalance( player );
        if ( cost > balance ) {
            Interactions.itemCostTooMuch( item, player, quantity, balance, cost );
            return true;
        }
        if ( cost < 0 ) {
            Interactions.itemsRunOut(item, player);
            return true;
        }
        int itemsGiven = processor.givePlayerItem(item, quantity, player);
        cost = processor.getBuyPrice(item, itemsGiven);
        if ( itemsGiven == 0 ){
            Interactions.noInventorySpace(item, player);
            return true;
        }
        else if ( itemsGiven < quantity)
            Interactions.inventorySpaceLimitBuy( item, itemsGiven, cost, player );
        else 
            Interactions.purchasedItems( item, quantity, cost, player);
        processor.removeItemsFromShop(item, itemsGiven);
        economy.withdrawPlayer(player, cost);
        costs.save();
        return true;
    }

    // Selling Items

    private boolean sellHand ( int quantity, Player player) {
        String item = processor.getHeldItem(player);
        Boolean valid = processor.isValidItem(player, item);
        if ( !valid ) return true;
        return sellItem(item, quantity, player);
    }

    private boolean sellItem ( String item, int quantity, Player player ) {
        Boolean valid = processor.isValidItem(player, item);
        if ( !valid ) return true;
        int soldAmount = processor.takeItemFromPlayer(item, quantity, player);
        double saleprice = processor.getSalePrice(item, soldAmount);

        if ( soldAmount == 0 ){
            Interactions.noItems(item, player);
            return true;
        }
        else if ( soldAmount < quantity)
            Interactions.saleShortItems( item, player, soldAmount, saleprice );
        else 
            Interactions.saleItems( item, player, soldAmount, saleprice );

        processor.insertItemIntoShop(item, soldAmount);
        economy.depositPlayer(player, saleprice);
        costs.save();
        return true;
    }

    private boolean sellAll ( Player player ){
        HashMap<String,Integer> soldItems = processor.removeAllValidFromInventory(player);
        double total = 0;
        int totalCount = 0;
        Interactions.saleHeader(player);
        for (HashMap.Entry<String, Integer> entry : soldItems.entrySet()) {
            double salePrice = processor.getSalePrice(entry.getKey(), entry.getValue());
            total += salePrice;
            totalCount += entry.getValue();
            Interactions.saleItemsCompact( entry.getKey(), player, entry.getValue(), salePrice );
            processor.insertItemIntoShop(entry.getKey(), entry.getValue());
        }
        Interactions.saleTotal(player, totalCount, total);
        economy.depositPlayer(player, total);
        costs.save();
        return true;
    }


}
