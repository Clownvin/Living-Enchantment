This mod adds living weapons, tools, and armor through the use of a new enchantment, Living! Living lets your equipment grow stronger over time, and even have their own personalities! This mod is HIGHLY configurable, with many config options to choose from (although I've designed the defaults to be as balanced for vanilla gameplay as I can)

Each enchanted item gains experience through use. There are 3 different types of ways for items to gain XP in this mod for you to chose from.

   Mending-Style: Living items will consume some of your XP to level up, similar to how mending works.
   Original: Living items gain XP instantly when killing a Mob or breaking a block (tools only)
   Original with XP orbs: Similar to original, except instead of instantly adding XP, it spawns an XP orb (orb only works with living items, it is not a normal XP orb)
 
Each level increases the damage and effectiveness of the weapon by a set amount, 5% by default. For armor, the level of all pieces worn is added up, then the damage is divided by 1 + (0.05 * combined level). You can change a lot about the leveling process and other rates in mod config, which you can find in the mod settings or you can edit the config file in the config folder inside %appdata%/.minecraft.

So how do you obtain the enchantment in survival? By default, you can get the enchantment book or a unique enchanted item from fishing (1 in 1000 default) or from any spawned chest (1 in 9 default). Villagers should also be able to sell them. Of course, if you're in creative you can just find the book in the Tools tab.

Because this enchantment is so strong (depending on how much you use the item), it is incompatible with all other damage or efficiency enchantments by default. Which is fine, you'll be able to outperform them if you put the time in. You can also enable other damage enchantments if you want, you cheeky dog. Protection is disabled by default for living armor too.

Enchantments also come with a personality. Each living item will respond to certain events in a way you might expect a person with that personality to respond! You can create your own personalities or edit existing ones in the %appdata%/.minecraft/config/personalities folder.

As far as compatibility with items other mods, this enchantment should work with any item from any mod, as long as the item returns a proper tool class Set (which most will). There is also an Enchanter recipe for this enchantment for the EnderIO mod.

This mod also contains a few commands, if you need/want to use them. All these commands require you to hold an item enchanted with Living in your mainhand, and will affect that item.

   additemxp <xp>  -  Adds xp to the item held in your main hand.
   setitemxp <xp>  -  Sets the xp of the item held in your main hand.
   setitemlevel <level>  -  Sets the level of the item held in your main hand.
   setpersonality [personality]  -  Sets the personality of the item held in your main hand. If you opt to leave personality blank (no arguments), then the command will list all available personalities instead.
   resetitem  -  Resets EVERYTHING for the item (level, xp, personality, all the counts)
