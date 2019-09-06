package me.randomhashtags.randompackage.utils;

public abstract class CustomEnchantUtils extends RPFeature {
    /*
    public String getIdentifier() { return "CUSTOM_ENCHANT_UTILS"; }

    public static HashMap<CustomEnchant, Integer> timerenchants;
    public static HashMap<Location, HashMap<ItemStack, HashMap<Block, Integer>>> temporaryblocks; // <block location <original block, <temporary new block, ticks>>>>

    private void doAttribute(CustomEnchantProcEvent e, String attribute, CustomEnchant enchant, Player P) {
        final int level = e.level;
        if(attribute.contains("level")) attribute = attribute.replace("level", Integer.toString(level));
        int b = -1;
        e.setCancelled(false);
        if(attribute.contains("random{")) {
            final String ee = attribute.split("random\\{")[1].split("}")[0];
            final int min = (int) evaluate(ee.split(":")[0]), max = (int) evaluate(ee.split(":")[1].split("}")[0]);
            int r = min + random.nextInt(max - min + 1);
            attribute = attribute.replace("random{" + ee + "}", Integer.toString(r));
        }
        for(String a : attribute.split(";")) {
            b++;
            if(a.toLowerCase().startsWith("didproc") && !e.didProc) {
                return;
            } else if(!a.equals(attribute.split(";")[0]) && !a.toLowerCase().startsWith("chance=") && (!attribute.toLowerCase().contains("chance=") || e.didProc)) {
                if(!attribute.toLowerCase().contains("chance=")) e.didProc = true;
                executeAttribute(e, e.event, enchant, a, attribute, b, P);
                if(a.toLowerCase().startsWith("wait{")) return;
            }
        }
    }

    public void executeAttribute(CustomEnchantProcEvent ev, Event event, CustomEnchant enchant, String a, String attribute, int b, Player P) {
        if(event != null && a.toLowerCase().startsWith("cancel")) {
            if(event instanceof Cancellable) {
                ((Cancellable) event).setCancelled(true);
            }
        } else {
            w(ev, event, enchant, getRecipients(event, a.contains("[") ? a.split("\\[")[1].split("]")[0] : a, P), a, attribute, b, P);
        }
    }
    public void w(CustomEnchantProcEvent ev, Event event, CustomEnchant enchant, List<LivingEntity> recipients, String a, String attribute, int b, Player P) {
        try {
            executeAttributes(ev, event, enchant, recipients, a, attribute, b, P);
        } catch (Exception e) {
            System.out.print(" ");
            System.out.print("[RandomPackage] Custom Enchant Exception caught. Below is the info that caused the error.");
            System.out.print("[RandomPackage] Version: " + randompackage.getDescription().getVersion() + ". User: %%__USER__%%");
            System.out.print("[RandomPackage] CustomEnchantProcEvent = " + ev);
            System.out.print("[RandomPackage] Event = " + event);
            System.out.print("[RandomPackage] Custom Enchant = " + (enchant != null ? enchant.getName() : "null"));
            System.out.print("[RandomPackage] recipients = " + recipients);
            System.out.print("[RandomPackage] a = " + a);
            System.out.print("[RandomPackage] attribute = " + attribute);
            System.out.print("[RandomPackage] b = " + b);
            System.out.print("[RandomPackage] P = " + P);
            System.out.print(" ");
            e.printStackTrace();
        }
    }

    private void executeAttributes(CustomEnchantProcEvent ev, Event event, CustomEnchant enchant, List<LivingEntity> recipients, String a, String attribute, int b, Player P) {
        if(ev != null && !ev.didProc) return;
        final Player player = ev != null ? ev.player : null;
        final int level = ev != null ? ev.level : 0;
        final boolean isPVAny = event instanceof PvAnyEvent;

        if(a.contains("random{")) {
            final String e = a.split("random\\{")[1].split("}")[0];
            final int min = (int) evaluate(e.split(":")[0]), max = (int) evaluate(e.split(":")[1].split("}")[0]);
            int r = min + random.nextInt(max - min + 1);
            a = a.replace("random{" + e + "}", Integer.toString(r));
        }
        if(a.contains("direction")) {
            final String type = "direction" + (a.contains("directionXOf") ? "X" : a.contains("directionYOf") ? "Y" : a.contains("directionZOf") ? "Z" : "") + "Of", r = a.split(type + "\\{")[1].split("}")[0];
            final LivingEntity recip = getRecipient(event, r);
            if(recip != null) {
                final Vector direc = recip.getLocation().getDirection();
                a = a.replace(type + "{" + r + "}", Double.toString(type.contains("X") ? direc.getX() : type.contains("Y") ? direc.getY() : type.contains("Z") ? direc.getZ() : 0.00));
            } else {
                Bukkit.broadcastMessage("[RandomPackage] recipient == null. Event=" + event.getEventName());
                return;
            }
        }
        if(a.contains("nearby{") || a.contains("nearbyAllies{") || a.contains("nearbyEnemies{")) {
            final boolean allies = a.contains("nearbyAllies{"), enemies = a.contains("nearbyEnemies{");
            final String e = a.split("nearby" + (allies ? "Allies" : enemies ? "Enemies" : "") + "\\{")[1].split("}")[0];
            final List<LivingEntity> r = new ArrayList<>();
            final LivingEntity who = event instanceof PluginEnableEvent ? P : getRecipient(event, e.split(":")[1]), k = getRecipient(event, e.split(":")[0]);
            if(who != null) {
                for(Entity en : who.getNearbyEntities(evaluate(e.split(":")[2]), evaluate(e.split(":")[3]), evaluate(e.split(":")[4]))) {
                    if(en instanceof LivingEntity && en instanceof Damageable && (k == null || k != null && !en.equals(k)))
                        if(!(en instanceof Player)
                                || who instanceof Player && en instanceof Player && (enemies && factions.isEnemy((Player) who, (Player) en) || allies && factions.isAlly((Player) who, (Player) en)))
                            r.add((LivingEntity) en);
                }
            }
            a = a.replace("nearby" + (allies ? "Allies" : enemies ? "Enemies" : "") + "{" + e + "}", r.toString().replace("\\p{Z}", ""));
            recipients = r;
        }
        if(a.contains("nearbySize{") || a.contains("nearbyAlliesSize{") || a.contains("nearbyEnemiesSize{")) {
            int size = 0;
            final boolean allies = a.contains("nearbyAlliesSize{"), enemies = a.contains("nearbyEnemiesSize{");
            final String e = a.split("nearby" + (allies ? "Allies" : enemies ? "Enemies" : "") + "Size\\{")[1].split("}")[0];
            final LivingEntity who = event instanceof PluginEnableEvent ? P : getRecipient(event, e.split(":")[1]), k = getRecipient(event, e.split(":")[0]);
            for(Entity en : who.getNearbyEntities(evaluate(e.split(":")[2]), evaluate(e.split(":")[3]), evaluate(e.split(":")[4]))) {
                if(en instanceof LivingEntity && en instanceof Damageable && (k == null || k != null && !en.equals(k)))
                    if(!allies && !(en instanceof Player)
                            || who instanceof Player && en instanceof Player && (enemies && factions.isEnemy((Player) who, (Player) en) || allies && factions.isAlly((Player) who, (Player) en)))
                        size += 1;
            }
            a = a.replace("nearby" + (allies ? "Allies" : enemies ? "Enemies" : "") + "Size{" + e + "}", Integer.toString(size));
        }

        if(a.toLowerCase().startsWith("procenchants{")) {
            for(LivingEntity l : recipients)
                if(l instanceof Player) {
                    final HashMap<ItemStack, HashMap<CustomEnchant, Integer>> enchants = getEnchants((Player) l);
                    for(ItemStack is : enchants.keySet()) {
                        final HashMap<CustomEnchant, Integer> e = enchants.get(is);
                        for(CustomEnchant ce : e.keySet())
                            if(!ce.getAttributes().toString().toLowerCase().contains("procenchants{"))
                                procEnchant(event, ce, e.get(ce), is, P);
                    }
                }
        } else if(a.toLowerCase().startsWith("breakblocks{")) {
            a = a.toLowerCase();
            final String[] A = a.split("]")[1].split(":");
            final int x1 = Integer.parseInt(A[0]), y1 = Integer.parseInt(A[1]), z1 = Integer.parseInt(A[2]);
            final int x2 = Integer.parseInt(A[3]), y2 = Integer.parseInt(A[4]), z2 = Integer.parseInt(A[5].split("}")[0]);
            for(LivingEntity le : recipients)
                if(event instanceof BlockBreakEvent)
                    breakBlocks(UMaterial.match(getItemInHand(le)), ((BlockBreakEvent) event).getBlock(), x1, y1, z1, x2, y2, z2);
        } else if(a.toLowerCase().startsWith("replaceblock{")) {
            final String args = a.toLowerCase().split("\\{")[1].split("}")[0];
            final String[] aa = args.split(":");
            final World w = player.getWorld();
            final int x = (int) evaluate(aa[0]), y = (int) evaluate(aa[1]), z = (int) evaluate(aa[2]);
            final Location l = new Location(w, x, y, z);
            final Material type = Material.valueOf(aa[3].toUpperCase());
            final Byte data = Byte.parseByte(aa[4]);
            final int ticks = Integer.parseInt(aa[5]);
            setTemporaryBlock(l, type, data, ticks);
        } else if(a.toLowerCase().startsWith("depletestacksize{") && event instanceof MobStackDepleteEvent) {
            final int amount = Integer.parseInt(a.split("\\{")[1].split("}")[0]);
            ((MobStackDepleteEvent) event).amount = amount;
        }
    }
    private boolean doVariable(CustomEnchantProcEvent e, Event event, CustomEnchant enchant, LivingEntity entity, String input) {
        if(input.startsWith("canBreakHitBlock")) return event instanceof PlayerInteractEvent && ((PlayerInteractEvent) event).getClickedBlock() != null && factions.canModify(((PlayerInteractEvent) event).getPlayer().getUniqueId(), ((PlayerInteractEvent) event).getClickedBlock().getLocation());
        else if(input.startsWith("didproc")) return e.didProc;
        else if(input.startsWith("didntproc")) return !e.didProc;
        else if(input.startsWith("enchantIs(")) {
            if(enchant != null) {
                final String inpu = input.split("enchantIs\\(")[1].split("\\\\")[0];
                for(String s : inpu.split("\\|\\|")) if(s.equals(enchant.getName())) return true;
            }
            return false;
        } else if(input.toLowerCase().startsWith("distancebetween(")) {
            final List<LivingEntity> recipients = getRecipients(event, input.split("distanceBetween\\(")[1].split("\\)")[0], null);
            if(recipients.size() == 2)
                return input.split("distanceBetween\\(")[1].split("\\)")[1].startsWith("<=") ? distanceBetween(recipients.get(0), recipients.get(1)) <= evaluate(input.split("\\)<=")[1]) : distanceBetween(recipients.get(0), recipients.get(1)) >= evaluate(input.split("\\)>=")[1]);
            return false;
        }
        return false;
    }
    public ItemStack getRarityGem(RarityGem gem, Player player) {
        final PlayerInventory pi = player.getInventory();
        final List<String> l = gem.getItem().getItemMeta().getLore();
        for(int i = 0; i < pi.getSize(); i++) {
            final ItemStack a = pi.getItem(i);
            if(a != null && a.hasItemMeta() && a.getItemMeta().hasLore() && a.getItemMeta().getLore().equals(l)) {
                return a;
            }
        }
        return null;
    }
    /*
        ATTRIBUTES
     *//*
    private void setTemporaryBlock(Location l, Material m, byte data, int ticks) {
        final World w = l.getWorld();
        final Block prev = w.getBlockAt(l);
        final Material prevm = prev.getType();
        final byte prevd = prev.getState().getRawData();
        if(!temporaryblocks.containsKey(l)) {
            temporaryblocks.put(l, new HashMap<>());
            final ItemStack a = new ItemStack(prevm, 1, prevd);
            temporaryblocks.get(l).put(a, new HashMap<>());
            final Block b = w.getBlockAt(l);
            b.setType(m);
            b.getState().setRawData(data);
            temporaryblocks.get(l).get(a).put(b, ticks);
            scheduler.scheduleSyncDelayedTask(randompackage, () -> {
                w.getBlockAt(l).setType(prevm);
                w.getBlockAt(l).getState().setRawData(prevd);
                temporaryblocks.remove(l);
            }, ticks);
        }
    }
    private int getXP(LivingEntity entity) { return entity instanceof Player ? getTotalExperience((Player) entity) : 0; }
    private void breakBlocks(UMaterial usedItem, Block b, int x1, int y1, int z1, int x2, int y2, int z2) {
        if(usedItem != null && b != null) {
            final World w = b.getWorld();
            final Location bl = b.getLocation();
            final int B1 = bl.getBlockX(), B2 = bl.getBlockY(), B3 = bl.getBlockZ();
            final int X1 = x1 > x2 ? x2 : x1, X2 = X1 == x2 ? x1 : x2;
            final int Y1 = y1 > y2 ? y2 : y1, Y2 = Y1 == y2 ? y1 : y2;
            final int Z1 = z1 > z2 ? z2 : z1, Z2 = Z1 == z2 ? z1 : z2;
            for(int x = B1 + X1; x <= B1 + X2; x++) {
                for(int y = B2 + Y1; y <= B2 + Y2; y++) {
                    for(int z = B3 + Z1; z <= B3 + Z2; z++) {
                        final Block block = w.getBlockAt(new Location(w, x, y, z));
                        if(canBeBroken(usedItem, block)) block.breakNaturally();
                    }
                }
            }
        }
    }
    private boolean canBeBroken(UMaterial usedItem, Block block) {
        final String b = UMaterial.getItem(block).name(), i = usedItem.name();
        final ConfigurationSection a = config.getConfigurationSection("block break blacklist");
        if(a != null) {
            for(String s : a.getKeys(false)) {
                if(s.equals("global") || i.endsWith(s.toUpperCase())) {
                    for(String ss : config.getStringList("block break blacklist." + s)) {
                        if(b.endsWith(ss.toUpperCase())) return false;
                    }
                }
            }
        }
        return true;
    }
    private double distanceBetween(Entity e1, Entity e2) {
        return e1.getLocation().distance(e2.getLocation());
    }
    private void setVelocity(LivingEntity entity, Vector vel) {
        entity.setVelocity(vel);
    }*/
}
