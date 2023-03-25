package me.randomhashtags.randompackage.supported.economy;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Vault {
    INSTANCE;

    private boolean didSetupEco = false;
    private Economy economy;
    public Chat chat;
    private Permission permission;

    public void setupEconomy() {
        didSetupEco = true;
        final RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if(economyProvider != null) {
            economy = economyProvider.getProvider();
        }
    }
    @Nullable
    public Economy getEconomy() {
        if(!didSetupEco) {
            setupEconomy();
        }
        return economy;
    }

    public Chat getChat() {
        return chat;
    }
    public boolean setupChat() {
        final RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp != null ? rsp.getProvider() : null;
        return chat != null;
    }

    public Permission getPermission() {
        return permission;
    }
    public boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        permission = rsp != null ? rsp.getProvider() : null;
        return permission != null;
    }
}
