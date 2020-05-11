package cc.bukkit.quickshop.addon.shopitemonly;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.shop.Shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ShopItemOnly extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void invClose(InventoryCloseEvent event){
        Shop shop = QuickShopAPI.getShopAPI().getShopIncludeAttached(Objects.requireNonNull(event.getInventory().getLocation()));
        if(shop == null){
            return;
        }
        List<ItemStack> pendingForRemoval = new ArrayList<>();
        for(ItemStack stack : event.getInventory().getStorageContents()){
            if(QuickShop.getInstance().getItemMatcher().matches(shop.getItem(),stack)){
                continue;
            }
            if(stack == null || stack.getType() == Material.AIR){
                continue;
            }
            pendingForRemoval.add(stack);
        }
        pendingForRemoval.forEach(item->{
            event.getInventory().remove(item);
            Objects.requireNonNull(event.getPlayer().getWorld()).dropItemNaturally(event.getPlayer().getLocation(),item);
        });
    }
    @EventHandler(priority = EventPriority.HIGH,ignoreCancelled = true)
    public void invClose(InventoryMoveItemEvent event){
        Shop shop = QuickShopAPI.getShopAPI().getShopIncludeAttachedWithCaching(Objects.requireNonNull(event.getDestination().getLocation()));
        if(shop == null){
            return;
        }
        if(QuickShop.getInstance().getItemMatcher().matches(shop.getItem(),event.getItem())){
            return;
        }
        event.setCancelled(true);
    }

}
