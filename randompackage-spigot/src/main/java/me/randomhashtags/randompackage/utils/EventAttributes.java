package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.api.events.FallenHeroSlainEvent;
import me.randomhashtags.randompackage.api.events.customenchant.*;
import me.randomhashtags.randompackage.api.events.PlayerClaimEnvoyCrateEvent;
import me.randomhashtags.randompackage.api.events.JackpotPurchaseTicketsEvent;
import me.randomhashtags.randompackage.api.events.ServerCrateOpenEvent;
import me.randomhashtags.randompackage.api.events.ShopPurchaseEvent;
import me.randomhashtags.randompackage.api.events.ShopSellEvent;
import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomEnchant;
import me.randomhashtags.randompackage.utils.abstraction.AbstractEnchantRarity;
import me.randomhashtags.randompackage.utils.classes.customenchants.CustomEnchant;
import me.randomhashtags.randompackage.utils.classes.customenchants.EnchantRarity;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public abstract class EventAttributes extends RPFeature {

    public String executeAttributes(Player player, EntityDeathEvent event, List<String> attributes) {
        String completion = "";
        if(player != null && event != null && attributes != null && !attributes.isEmpty()) {
            final LivingEntity killed = event.getEntity();
            final UUID u = killed.getUniqueId();
            final String type = killed.getType().name().toLowerCase();
            final boolean killedPlayer = killed instanceof Player, isCustomMob = !killedPlayer && RPPlayer.get(player.getUniqueId()).getCustomEnchantEntities().contains(u);
            final String j = killedPlayer ? "killedplayer;" : "killedentity";
            for(String s : attributes) {
                final String a = s.toLowerCase();
                boolean did = true;
                if(a.startsWith(j)) {
                    for(String A : a.split(j)[1].split(";")) {
                        if(A.startsWith("type=")) {
                            did = type.equals(A.split("=")[1]);
                        } else if(A.startsWith("isenemy")) {
                            did = !killedPlayer && !isCustomMob || killedPlayer && fapi != null && fapi.relationIsEnemyOrNull(player, (Player) killed);
                        } else if(did) {
                            completion = completion.concat(A + "&&");
                        }
                    }
                }
            }
        }
        return completion.isEmpty() ? null : completion.substring(0, completion.length()-2);
    }
    public String executeAttributes(Player player, JackpotPurchaseTicketsEvent event, List<String> attributes) {
        String completion = "";
        if(player != null && event != null && attributes != null && !attributes.isEmpty()) {
            final String price = Double.toString(event.price), amount = Integer.toString(event.amount);
            for(String s : attributes) {
                final String a = s.toLowerCase();
                boolean did = true;
                if(a.startsWith("jackpotpurchasetickets;")) {
                    for(String A : a.split("jackpotpurchasetickets;")[1].split(";")) {
                        if(did) {
                            completion = completion.concat(A + "&&").replace("price", price).replace("amount", amount);
                        }
                    }
                }
            }
        }
        return completion.isEmpty() ? null : completion.substring(0, completion.length()-2);
    }
    public String executeAttributes(Player player, ServerCrateOpenEvent event, List<String> attributes) {
        String completion = "";
        if(player != null && event != null && attributes != null && !attributes.isEmpty()) {
            final boolean cancelled = event.isCancelled();
            final String rarity = event.crate.getYamlName();
            for(String s : attributes) {
                final String a = s.toLowerCase();
                boolean did = true;
                if(a.startsWith("servercrateopen;")) {
                    final String[] original = s.split(s.split(";")[0] + ";")[1].split(";");
                    int i = 0;
                    for(String A : a.split("servercrateopen;")[1].split(";")) {
                        if(A.startsWith("cancelled=")) {
                            did = cancelled && Boolean.parseBoolean(A.split("=")[1]);
                        } else if(A.startsWith("rarity=")) {
                            did = original[i].split("=")[1].equals(rarity);
                        } else if(did) {
                            completion = completion.concat(A + "&&").replace("rarity", rarity);
                        }
                        i++;
                    }
                }
            }
        }
        return completion.isEmpty() ? null : completion.substring(0, completion.length()-2);
    }

    public String executeAttributes(Player player, ShopPurchaseEvent event, List<String> attributes) {
        return player != null && event != null && attributes != null && !attributes.isEmpty() ? returnShop( "shoppurchaseitem", Double.toString(event.cost), UMaterial.match(event.item).name(), attributes) : null;
    }
    public String executeAttributes(Player player, ShopSellEvent event, List<String> attributes) {
        return player != null && event != null && attributes != null && !attributes.isEmpty() ? returnShop( "shopsellitem", Double.toString(event.profit), UMaterial.match(event.item).name(), attributes) : null;
    }
    private String returnShop(String type, String d, String material, List<String> attributes) {
        String completion = "";
        final String t = type.equals("shopsellitem") ? "profit" : "cost";
        for(String s : attributes) {
            final String a = s.toLowerCase();
            boolean did = true;
            if(a.startsWith(type + ";")) {
                for(String A : a.split( type + ";")[1].split(";")) {
                    if(A.startsWith("material=")) {
                        did = material.endsWith(A.split("=")[1].toUpperCase());
                    } else if(did) {
                        completion = completion.concat(A + "&&").replace(t, d);
                    }
                }
            }
        }
        return completion.isEmpty() ? null : completion.substring(0, completion.length()-2);
    }

    public String executeAttributes(Player player, BlockBreakEvent event, List<String> attributes) {
        String completion = "";
        if(player != null && event != null && attributes != null && !attributes.isEmpty()) {
            final boolean cancelled = event.isCancelled();
            final UMaterial u = UMaterial.match(player.getItemInHand());
            final String material = u != null ? u.name() : "AIR";
            for(String s : attributes) {
                final String a = s.toLowerCase();
                boolean did = true;
                if(a.startsWith("blockmined;")) {
                    for(String A : a.split("blockmined;")[1].split(";")) {
                        if(A.startsWith("cancelled=")) {
                            did = cancelled && Boolean.parseBoolean(A.split("=")[1]);
                        } else if(A.startsWith("material=")) {
                            did = material.endsWith(A.split("=")[1].toUpperCase());
                        } else if(did) {
                            completion = completion.concat(A + "&&");
                        }
                    }
                }
            }
        }
        return completion.isEmpty() ? null : completion.substring(0, completion.length()-2);
    }
    public String executeAttributes(Player player, FallenHeroSlainEvent event, List<String> attributes) {
        String completion = "";
        if(player != null && event != null && attributes != null && !attributes.isEmpty()) {
            final boolean droppedGem = event.didDropGem;
            final UMaterial u = UMaterial.match(player.getItemInHand());
            final String material = u != null ? u.name() : "AIR";
            for(String s : attributes) {
                final String a = s.toLowerCase();
                boolean did = true;
                if(a.startsWith("fallenheroslain;")) {
                    for(String A : a.split("fallenheroslain;")[1].split(";")) {
                        if(A.startsWith("droppedgem=")) {
                            did = droppedGem && Boolean.parseBoolean(A.split("=")[1]);
                        } else if(A.startsWith("material=")) {
                            did = material.endsWith(A.split("=")[1].toUpperCase());
                        } else if(did) {
                            completion = completion.concat(A + "&&");
                        }
                    }
                }
            }
        }
        return completion.isEmpty() ? null : completion.substring(0, completion.length()-2);
    }
    public String executeAttributes(Player player, RandomizationScrollUseEvent event, List<String> attributes) {
        String completion = "";
        if(player != null && event != null && attributes != null && !attributes.isEmpty()) {
            final boolean cancelled = event.isCancelled();
            final int newSuccess = event.getNewSuccess(), newDestroy = event.getNewDestroy();
            final String customenchant = event.customenchant.getYamlName(), rarity = event.scroll.getPath();
            for(String s : attributes) {
                final String a = s.toLowerCase();
                boolean did = true;
                if(a.startsWith("randomizationscrollused;")) {
                    final String[] original = s.split(s.split(";")[0] + ";")[1].split(";");
                    int i = 0;
                    for(String A : a.split("randomizationscrollused;")[1].split(";")) {
                        if(A.startsWith("cancelled=")) {
                            did = cancelled && Boolean.parseBoolean(A.split("=")[1]);
                        } else if(A.startsWith("customenchant=")) {
                            did = customenchant.equals(A.split("=")[1].toUpperCase());
                        } else if(A.startsWith("newsuccess=")) {
                            did = newSuccess == Integer.parseInt(A.split("=")[1]);
                        } else if(A.startsWith("newdestroy=")) {
                            did = newDestroy == Integer.parseInt(A.split("=")[1]);
                        } else if(A.startsWith("rarity=")) {
                            did = original[i].split("=")[1].equals(rarity);
                        } else if(did) {
                            completion = completion.concat(A + "&&");
                        }
                        i++;
                    }
                }
            }
        }
        return completion.isEmpty() ? null : completion.substring(0, completion.length()-2);
    }
    public String executeAttributes(Player player, PlayerClaimEnvoyCrateEvent event, List<String> attributes) {
        String completion = "";
        if(player != null && event != null && attributes != null && !attributes.isEmpty()) {
            final boolean cancelled = event.isCancelled();
            final String tier = event.type.getType().getYamlName();
            for(String s : attributes) {
                final String a = s.toLowerCase();
                boolean did = true;
                if(a.startsWith("envoycratelooted;")) {
                    final String[] original = s.split(s.split(";")[0] + ";")[1].split(";");
                    int i = 0;
                    for(String A : a.split("envoycratelooted;")[1].split(";")) {
                        if(A.startsWith("cancelled=")) {
                            did = cancelled && Boolean.parseBoolean(A.split("=")[1]);
                        } else if(A.startsWith("tier=")) {
                            did = original[i].split("=")[1].equals(tier);
                        } else if(did) {
                            completion = completion.concat(A + "&&");
                        }
                        i++;
                    }
                }
            }
        }
        return completion.isEmpty() ? null : completion.substring(0, completion.length()-2);
    }
    public String executeAttributes(Player player, PlayerApplyCustomEnchantEvent event, List<String> attributes) {
        String completion = "";
        if(player != null && event != null && attributes != null && !attributes.isEmpty()) {
            final AbstractCustomEnchant enchant = event.enchant;
            final String rarity = EnchantRarity.valueOf(enchant).getName(), name = enchant.getYamlName(), result = event.result.name();
            for(String s : attributes) {
                final String a = s.toLowerCase();
                boolean did = true;
                if(a.startsWith("customenchantapplied;")) {
                    final String[] original = s.split(s.split(";")[0] + ";")[1].split(";");
                    int i = 0;
                    for(String A : a.split("customenchantapplied;")[1].split(";")) {
                        if(A.startsWith("result=")) {
                            did = A.split("=")[1].toUpperCase().equals(result);
                        } else if(A.startsWith("enchant=")) {
                            did = original[i].split("=")[1].equals(name);
                        } else if(A.startsWith("rarity=")) {
                            did = original[i].split("=")[1].equals(rarity);
                        } else if(did) {
                            completion = completion.concat(A + "&&");
                        }
                        i++;
                    }
                }
            }
        }
        return completion.isEmpty() ? null : completion.substring(0, completion.length()-2);
    }
    public String executeAttributes(Player player, EnchanterPurchaseEvent event, List<String> attributes) {
        String completion = "";
        if(player != null && event != null && attributes != null && !attributes.isEmpty()) {
            final ItemStack is = event.purchased;
            final AbstractEnchantRarity r = EnchantRarity.valueOf(is);
            final boolean cancelled = event.isCancelled(), isRarityBook = r != null;
            final String rarity = isRarityBook ? r.getName() : null;
            for(String s : attributes) {
                final String a = s.toLowerCase();
                boolean did = true;
                if(a.startsWith("enchanterpurchase;")) {
                    final String[] original = s.split(s.split(";")[0] + ";")[1].split(";");
                    int i = 0;
                    for(String A : a.split("enchanterpurchase;")[1].split(";")) {
                        if(A.startsWith("israritybook")) {
                            did = isRarityBook;
                        } else if(A.startsWith("cancelled=")) {
                            did = cancelled && Boolean.parseBoolean(A.split("=")[1]);
                        } else if(A.startsWith("rarity=")) {
                            did = original[i].split("=")[1].equals(rarity);
                        } else if(did) {
                            completion = completion.concat(A + "&&");
                        }
                        i++;
                    }
                }
            }
        }
        return completion.isEmpty() ? null : completion.substring(0, completion.length()-2);
    }
    public String executeAttributes(Player player, AlchemistExchangeEvent event, List<String> attributes) {
        String completion = "";
        if(player != null && event != null && attributes != null && !attributes.isEmpty()) {
            final boolean cancelled = event.isCancelled();
            final String rarity = EnchantRarity.valueOf(CustomEnchant.valueOf(event.result)).getName();
            for(String s : attributes) {
                final String a = s.toLowerCase();
                boolean did = true;
                if(a.startsWith("alchemistexchange;")) {
                    final String[] original = s.split(s.split(";")[0] + ";")[1].split(";");
                    int i = 0;
                    for(String A : a.split("alchemistexchange;")[1].split(";")) {
                        if(A.startsWith("cancelled=")) {
                            did = cancelled && Boolean.parseBoolean(A.split("=")[1]);
                        } else if(A.startsWith("rarity=")) {
                            did = original[i].split("=")[1].equals(rarity);
                        } else if(did) {
                            completion = completion.concat(A + "&&");
                        }
                        i++;
                    }
                }
            }
        }
        return completion.isEmpty() ? null : completion.substring(0, completion.length()-2);
    }
    public String executeAttributes(Player player, ItemNameTagUseEvent event, List<String> attributes) {
        String completion = "";
        if(player != null && event != null && attributes != null && !attributes.isEmpty()) {
            final String material = event.item.getType().name(), renamedto = event.renamedTo;
            final boolean cancelled = event.isCancelled();
            for(String s : attributes) {
                final String a = s.toLowerCase();
                boolean did = true;
                if(a.startsWith("itemnametagused;")) {
                    final String[] original = s.split(s.split(";")[0] + ";")[1].split(";");
                    int i = 0;
                    for(String A : a.split("itemnametagused;")[1].split(";")) {
                        if(A.startsWith("cancelled=")) {
                            did = cancelled && Boolean.parseBoolean(A.split("=")[1]);
                        } else if(A.startsWith("material=")) {
                            did = material.endsWith(A.split("=")[1].toUpperCase());
                        } else if(A.startsWith("renamedto=")) {
                            did = renamedto.equals(original[i].split("=")[1]);
                        } else if(did) {
                            completion = completion.concat(A + "&&");
                        }
                        i++;
                    }
                }
            }
        }
        return completion.isEmpty() ? null : completion.substring(0, completion.length()-2);
    }
    public String executeAttributes(Player player, MysteryMobSpawnerOpenEvent event, List<String> attributes) {
        String completion = "";
        if(player != null && event != null && attributes != null && !attributes.isEmpty()) {
            final String entity = event.entity.toUpperCase();
            final boolean cancelled = event.isCancelled();
            for(String s : attributes) {
                final String a = s.toLowerCase();
                boolean did = true;
                if(a.startsWith("mysterymobspawneropened;")) {
                    for(String A : a.split("mysterymobspawneropened;")[1].split(";")) {
                        if(A.startsWith("cancelled=")) {
                            did = cancelled && Boolean.parseBoolean(A.split("=")[1]);
                        } else if(A.startsWith("entity=")) {
                            did = entity.equals(A.split("=")[1].toUpperCase());
                        } else if(did) {
                            completion = completion.concat(A + "&&");
                        }
                    }
                }
            }
        }
        return completion.isEmpty() ? null : completion.substring(0, completion.length()-2);
    }
}
