package me.randomhashtags.randompackage.utils.supported;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultAPI {
    private static VaultAPI instance;
    public static final VaultAPI getVaultAPI() {
        if(instance == null) instance = new VaultAPI();
        return instance;
    }

    public Economy economy = null;
    public Chat chat = null;
    public Permission perms = null;
    public boolean setupEconomy() {
        final RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if(economyProvider != null) { economy = economyProvider.getProvider(); } return (economy != null);
    }
    public boolean setupChat() {
        final RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp != null ? rsp.getProvider() : null;
        return chat != null;
    }
    public boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp != null ? rsp.getProvider() : null;
        return perms != null;
    }
}
