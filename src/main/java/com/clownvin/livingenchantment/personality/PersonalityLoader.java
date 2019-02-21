package com.clownvin.livingenchantment.personality;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PersonalityLoader {
    public static final File personalityLocation = new File("./config/personalities/");

    public static Personality[] getPersonalities() {
        ArrayList<Personality> personalities = new ArrayList<>(10);
        if (!personalityLocation.exists()) {
            personalityLocation.mkdirs();
        }
        createBasePersonalities();
        File[] personalityConfigs = personalityLocation.listFiles();
        for (File personalityConfig : personalityConfigs) {
            if (!personalityConfig.getName().endsWith(".cfg"))
                continue;
            personalities.add(loadPersonality(personalityConfig));
        }
        personalities.add(Personality.HEROBRINE);
        return personalities.toArray(new Personality[personalities.size()]);
    }

    public static void createPersonalityFile(Personality p) {
        File file = new File("./config/personalities/" + p.name + ".cfg");
        if (file.exists())
            return;
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("[name]\n" + p.name + "\n[weight]\n" + p.weight + "\n[use]\n");
            for (int i = 0; i < p.onUse.length; i++) {
                writer.write(p.onUse[i]);
                writer.newLine();
            }
            writer.write("[useweight]\n" + p.useOdds + "\n[kill]\n");
            for (int i = 0; i < p.onKill.length; i++) {
                writer.write(p.onKill[i]);
                writer.newLine();
            }
            writer.write("[killweight]\n" + p.killOdds + "\n[death]\n");
            for (int i = 0; i < p.onDeath.length; i++) {
                writer.write(p.onDeath[i]);
                writer.newLine();
            }
            writer.write("[levelup]\n");
            for (int i = 0; i < p.onLevelUp.length; i++) {
                writer.write(p.onLevelUp[i]);
                writer.newLine();
            }
            writer.write("[hurt]\n");
            for (int i = 0; i < p.onHurt.length; i++) {
                writer.write(p.onHurt[i]);
                writer.newLine();
            }
            writer.write("[hurtweight]\n" + p.hurtOdds + "\n[25%durability]\n");
            for (int i = 0; i < p.twentyPercent.length; i++) {
                writer.write(p.twentyPercent[i]);
                writer.newLine();
            }
            writer.write("[10%durability]\n");
            for (int i = 0; i < p.fivePercent.length; i++) {
                writer.write(p.fivePercent[i]);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createBasePersonalities() {
        createPersonalityFile(new Personality(10, "Lewd",
                new String[]{ //Use
                        "Yes... yes.. yes!!!",
                        "Use me more, $user! Yes!",
                        "I love it when you use me like that, $user.",
                        "Oh $user, you know just what I want!",
                        "Yes, $user! Yes! Use me JUST like that!",
                        "Oh god it feels so good, $user!",
                        "More! more! MORE!",
                        "God this just feels SO good!",
                        "$user, I hope this never stops!",
                        "$user, I feel so good!",
                        "Keep going, $user!",
                        "Oh yes, just like that $user!",
                        "Quick $user, quick! More!",
                        "Yeeeees! It's so GOOD!",
                        "Oh my gosh, $user, it just feels so good!",
                        "I'll never be able to get enough of this!",
                        "I need more, $user!",
                        "More!",
                        "Yes!",
                        "Give it to me, $user!",
                },
                7,
                new String[]{ //Kill
                        "They died!",
                },
                80,
                new String[]{ //Death
                        "Aww, did you die, $user? Hurry back!",
                        "Don't die on me, $user! I need you!",
                        "Hurry back! I need you, $user!",
                        "Don't leave me here, $user! I need to be used!"
                },
                new String[]{ //Level Up
                        "Oh it feels so good, $user! (Level $level)",
                        "My power is growing, $user! (Level $level)",
                        "I feel so reinvigorated! (Level $level)",
                        "So this what living feels like! (Level $level)",
                        "This power... it feels so good! (Level $level)",
                        "I could get used to this... (Level $level)"
                },
                new String[]{ //On Hurt
                        "I bet that one hurt.",
                        "It's not supposed to feel good.",
                        "I wish $user would slap me around like this!",
                        "Just a little more, $user.",
                },
                1,
                new String[]{ //Twenty percent
                        "I'm growing weaker, $user. ($durability durability remaining)",
                        "I'm starting to feel weak, $user. ($durability durability remaining)",
                        "Don't forget about me, $user! ($durability durability remaining)",
                },
                new String[]{ //Five percent
                        "I don't feel good at all... ($durability durability remaining!)",
                        "This can't be how this ends, $user! ($durability durability remaining!)",
                        "I'm not ready to die yet, $user! ($durability durability remaining!)",
                }));
        createPersonalityFile(new Personality(10, "Live Wire",
                new String[]{ //Use
                        "Yay!",
                        "I did it, $user! AWESOME!",
                        "Ooof, that one was rough.",
                        "Don't stop, $user! This is too much fun!",
                        "All this excercise feels so good, $user!",
                        "Lets keep at it, $user!",
                },
                24,
                new String[]{ //Kill
                        "DIE DIE DIE! Yes!",
                        "Die!",
                        "Ha! They died!",
                        "Heheh, that had to hurt.",
                        "Die die die!",
                },
                5,
                new String[]{ //Death
                        "You uh... you died.",
                        "You're comming back for me, right $user?",
                        "Don't leave me here, $user!!!",
                },
                new String[]{ //Level Up
                        "Yas! More POWER! (Level $level)",
                        "I feel so strong! (Level $level)",
                        "$user! $user! Look how strong I am now! (Level $level)",
                        "Am I awesome or what, $user?! (Level $level)",
                        "More... POWER! (Level $level)",
                },
                new String[]{ //On Hurt
                        "Ooof, that had to hurt!",
                        "Take that!",
                        "Yah! Have a little of that!",
                        "Haven't had enough yet?",
                        "There's plently more where that came from!"
                },
                14,
                new String[]{ //Twenty percent
                        "I'm growing weaker, $user. ($durability durability remaining)",
                        "I'm starting to feel weak, $user. ($durability durability remaining)",
                        "Don't forget about me, $user! ($durability durability remaining)",
                },
                new String[]{ //Five percent
                        "I don't feel good at all... ($durability durability remaining!)",
                        "This can't be how this ends, $user! ($durability durability remaining!)",
                        "I'm not ready to die yet, $user! ($durability durability remaining!)",
                }));
        createPersonalityFile(new Personality(10, "Lazy",
                new String[]{ //Use
                        "That's it. We're done... right?",
                        "Hey, $user, we're done now, right?",
                        "Please no more...",
                        "$user, please, I can't take it anymore!",
                        "I don't know how much more work I can take!",
                        "$user, I'm not sure I'll last much longer!",
                        "All this work is killing me..",
                        "Wow, $user, this is exhausting work!",
                        "Do you really need to do this? Wouldn't you rather go sleep? I would...",
                        "C'mon, lets go sleep now. I'm tired of working...",
                        "$user, enough work. Lets go take a nap!",
                        "No more! Please! I'm begging you, $user",
                        "Are we done yet?"
                },
                24,
                new String[]{ //Kill
                        "Look, $user, they're dead.",
                        "Nice one, $user!",
                        "Killing is so much work! I'm so tired!",
                        "Do we have to kill them, $user? Can't we just sleep?",
                        "I just want a break, $user"
                },
                12,
                new String[]{ //Death
                        "Finally, I can rest too!",
                        "Ouch! You look like you're in pain, $user.",
                        "Make sure to come back for me, $user!",
                        "Don't forget about me, $user!"
                },
                new String[]{ //Level Up
                        "Ah yes, more power! Now back to sleep... (Level $level)",
                        "I can feel the power flowing through me, $user! ... and now I'm bored. (Level $level)",
                        "I feel... strong, $user (Level $level)!",
                        "Is this what it feels like to be good for something? (Level $level)"
                },
                new String[]{ //On Hurt
                        "Ouch! I bet they'll feel that one tomorrow, $user!",
                        "Ooo, that looks like it had to hurt!",
                        "Ouch!",
                        "All this combat is making me so tired, $user!",
                        "Take that!"
                },
                16,
                new String[]{ //Twenty percent
                        "I'm growing weaker, $user. ($durability durability remaining)",
                        "I'm starting to feel weaker, $user. ($durability durability remaining)",
                        "Don't forget about me, $user! ($durability durability remaining)",
                },
                new String[]{ //Five percent
                        "Is the end finally here? ($durability durability remaining!)",
                        "Is this where we part ways, $user? ($durability durability remaining!)",
                        "I'm not ready to die yet, $user! ($durability durability remaining!)",
                }));
        createPersonalityFile(new Personality(10, "Depressed",
                new String[]{ //Use
                        "$user, can this wait? I'm really tired.",
                        "Do you really need to use me now, $user?",
                        "...Sigh...",
                        "I just wanna sleep...",
                        "Are we almost done yet, $user? I'm really tired.",
                },
                48,
                new String[]{ //Kill
                        "They're better off now anyway, $user",
                        "It's unfortunate I can't get you to kill me instead, $user",
                        "...Lucky",
                        "If only..."
                },
                22,
                new String[]{ //Death
                        "No $user, Not you too!",
                        "Don't leave me here, $user! Take me with you!",
                        "You're lucky, $user...",
                        "$user, I wish I was you... If I only was so fortunate"
                },
                new String[]{ //Level Up
                        "$user, I feel a bit better ($level)",
                        "Hey, $user, I'm starting to feel better... nope, wait, I'm not. ($level)",
                        "I don't deserve this, $user ($level)",
                        "Why help me, $user? ($level)",
                        "$user, just stop. I don't need your sympathy ($level).",
                },
                new String[]{ //On Hurt
                        "That's not very nice of you, $user...",
                        "Maybe you shouldn't hurt others, $user...",
                        "$user, is that really nice of you?",
                        "$user, when will you stop swinging me around? I'm tired.",
                },
                18,
                new String[]{ //Twenty percent
                        "My weakness is growing, $user. ($durability durability remaining)",
                        "I'm starting to feel weaker, $user. ($durability durability remaining)",
                        "Don't forget about me, $user! ($durability durability remaining)",
                },
                new String[]{ //Five percent
                        "Is the end finally here? ($durability durability remaining!)",
                        "Is this where we part ways, $user? ($durability durability remaining!)",
                        "I'm ready, $user... ($durability durability remaining!)",
                }));
        createPersonalityFile(new Personality(10, "Demonic",
                new String[]{ //Use
                        "sqaeae aoze",
                        "iz yiz yixjak qae xa song?",
                        "gae raeq yort za",
                        "ona esaeu tired esaq, suzor?"
                },
                32,
                new String[]{ //Kill
                        "ya mchl't iz iegd",
                        "yiz aera iz zira",
                        "yaes labbia o maeaeh",
                        "yaes kaeq s'oq yaes dralsjik",
                        "$user, yoq iz maen esaeu"
                },
                8,
                new String[]{ //Death
                        "Maeaeh, sael gona esaeu gia aer za!",
                        "I ohloesz tral esaeu labbia laot",
                        "Doyaqiy",
                        "$user, gae raeq gia aer za!",
                        "$user, rae!"
                },
                new String[]{ //Level Up
                        "Zes daelabbi grows qae chwah! (Level $level)",
                        "za maah zae ednaerdh, rael yoq za chwah! (Level $level)",
                        "I zaa ya laenhg. (Level $level)",
                        "$user, xa yonamuh, I oz rael chwaw! (Level $level)"
                },
                new String[]{ //On Hurt
                        "Qoeda zes wedrk",
                        "Esaeu l'tozz kazk!",
                        "esaeu lizz moih",
                        "re zeebnir ez oep.",
                        "qoeda qnua doir!",
                        "esaeu maeaehil't ynaoquna!",
                        "chq uz vanquish yiz foe, $user!",
                        "$user, yiz ynaoquna gaeaz raeq trael aeun ednardhy! chq uz l'tael iq!",
                        "Esaeu lizz trael haeza",
                        "$user, yiz ynaoquna zoes raeq xa allowed qae apied!",
                },
                12,
                new String[]{ //Twenty percent
                        "$user, ao bnzl zegsl ni jlmgbo dlwblhld! ($durability durability remaining)",
                        "Oep apih jeh zegvlh he rlmb al, $user! ($durability durability remaining)",
                        "Ao lmcjlii vgei... ($durability durability remaining)",
                },
                new String[]{ //Five percent
                        "Oep mjh hrmh n irepbd wlgnir, $user? ($durability durability remaining!)",
                        "Ro ej'h oep rlbw al, $user? ($durability durability remaining!)",
                        "N ma jlmgbo dlihgeold, $user! ($durability durability remaining!)",
                }));
        createPersonalityFile(new Personality(10, "Silent",
                new String[]{ //Use
                        "..."
                },
                40,
                new String[]{ //Kill
                        "..."
                },
                40,
                new String[]{ //Death
                        "..."
                },
                new String[]{ //Level Up
                        "... (Level $level)"
                },
                new String[]{ //On Hurt
                        "..."
                },
                40,
                new String[]{ //Twenty percent
                        "... ($durability durability remaining)",
                },
                new String[]{ //Five percent
                        "... ($durability durability remaining)",
                }));
        createPersonalityFile(new Personality(10, "Cat",
                new String[]{ //Use
                        "Meow.",
                        "Meeoow.",
                        "Meew.",
                        "Hiss!",
                        "Meow?"
                },
                24,
                new String[]{ //Kill
                        "Meow!",
                        "Hiss!",
                        "Hisss!",
                },
                12,
                new String[]{ //Death
                        "Meow...",
                        "Meow... meow?",
                        "Meow...?"
                },
                new String[]{ //Level Up
                        "Meow! (Level $level)"
                },
                new String[]{ //On Hurt
                        "Meow!",
                        "Hiss!",
                        "Hiss!",
                        "Hisss!"
                }, 8,
                new String[]{ //Twenty percent
                        "Meow! ($durability durability remaining)",
                        "Meow? ($durability durability remaining)",
                },
                new String[]{ //Five percent
                        "Growl! ($durability durability remaining)",
                        "Meow! ($durability durability remaining)",
                        "Meow??? Bark Bark! ($durability durability remaining)",
                }));
        createPersonalityFile(new Personality(10, "Dog",
                new String[]{ //Use
                        "Woof.",
                        "Howl.",
                        "Whimper.",
                        "Bark! Bark! Bark!",
                        "*Sniffs*"
                },
                24,
                new String[]{ //Kill
                        "Bark!",
                        "Bark bark bark!",
                        "Bark! Bark! Bark!"
                },
                12,
                new String[]{ //Death
                        "Whimper.",
                        "Whine.",
                },
                new String[]{ //Level Up
                        "Bark! (Level $level)",
                        "Woof! (Level $level)"
                },
                new String[]{ //On Hurt
                        "Woof!",
                        "Bark!",
                        "Bark bark bark!",
                        "Howl.",
                        "Low growl."
                }, 8,
                new String[]{ //Twenty percent
                        "Wimpers. ($durability durability remaining)",
                        "Whine. ($durability durability remaining)",
                        "Bark! Bark Bark! ($durability durability remaining)",
                },
                new String[]{ //Five percent
                        "Wimpers. ($durability durability remaining)",
                        "Whine. ($durability durability remaining)",
                        "Bark! Bark Bark! ($durability durability remaining)",
                }));
        createPersonalityFile(new Personality(10, "Glitchy",
                new String[]{ //Use
                        "§k c  drtbs fn  Wsneathc gt n  e. ceiapk½p  s t+thicFe  dce",
                        "§kemono,h    ?. eesf echh²aeane=e.TetVtd e²e½t.",
                        "§k slw  ½i  e b  wfd ovty t sa½wtuhHirchrt iecu)aoirc",
                        "§kcaygVennenr slw  ½i H",
                        "§k b  wfd ovty t sa½wtuhHirchrt iecu)aoir"
                },
                24,
                new String[]{ //Kill
                        "§k caf  lwu s  derh=ccn wae epo a    a   x",
                        "§kyV upneiehkah",
                        "§kl(  un tnyW  arta( "
                },
                12,
                new String[]{ //Death
                        "§kshs( i t. h oune pyf atw  oosWha rts tbmedhhe( r  = fei)lWifpndi s a tr b(ay t u cb  caf  lwu s",
                        "§kneiehkah tfc eept ½mo  ete +fla)tl a ky e  neep rlc lw u nx knmcT es nqimq .i ol ot c c/qon ",
                        "§ktalhl(  un tnyW"
                },
                new String[]{ //Level Up
                        "§kpe ecofs soyasd§r (Level $level)",
                        "§kt morsa ) kam detiriti §r (Level $level)",
                        "§kso+ Wt)ci  ten idqr k om n²tia rron = i o§r (Level $level)"
                },
                new String[]{ //On Hurt
                        "§kKary  d",
                        "§kape /nhros,n h",
                        "§kk pyn cr i  i  tkho ic d(m  t)t",
                        "§krsdile"
                }, 8,
                new String[]{ //Twenty percent
                        "§km a displacement kinetic energy of the resultant x? We caus§r ($durability durability remaining)",
                        "§kin call product of a particle o§r ($durability durability remaining)",
                        "§k then stant and V - ½ ( ½ ( V is the represe§r ($durability durability remaining)",
                },
                new String[]{ //Five percent
                        "§kd the squationstant x? We mas§r ($durability durability remaining!)",
                        "§krticle of the resent acti§r ($durability durability remaining!)",
                        "§k stant and V - ½ ( ½ ( V is the represent kineticle on a partic energy of its speed at t§r ($durability durability remaining!)",
                }));
        createPersonalityFile(new Personality(10, "Alien",
                new String[]{ //Use
                        "⌇⍾⏃⟒⏃⟒ ⏃⍜⋉⟒",
                        "⟟⋉ ⊬⟟⋉ ⊬⟟⌖⟊⏃☍ ⍾⏃⟒ ⌖⏃ ⌇⍜⋏☌?",
                        "☌⏃⟒ ⍀⏃⟒⍾ ⊬⍜⍀⏁ ⋉⏃",
                        "⍜⋏⏃ ⟒⌇⏃⟒⎍ ⏁⟟⍀⟒⎅ ⟒⌇⏃⍾, ⌇⎍⋉⍜⍀?"
                },
                32,
                new String[]{ //Kill
                        "⊬⏃ ⋔☊⊑⌰'⏁ ⟟⋉ ⟟⟒☌⎅",
                        "⊬⟟⋉ ⏃⟒⍀⏃ ⟟⋉ ⋉⟟⍀⏃",
                        "⊬⏃⟒⌇ ⌰⏃⏚⏚⟟⏃ ⍜ ⋔⏃⟒⏃⟒⊑",
                        "⊬⏃⟒⌇ ☍⏃⟒⍾ ⌇'⍜⍾ ⊬⏃⟒⌇ ⎅⍀⏃⌰⌇⟊⟟☍",
                        "⎍⌇⟒⍀, ⊬⍜⍾ ⟟⋉ ⋔⏃⟒⋏ ⟒⌇⏃⟒⎍"
                },
                8,
                new String[]{ //Death
                        "⋔⏃⟒⏃⟒⊑, ⌇⏃⟒⌰ ☌⍜⋏⏃ ⟒⌇⏃⟒⎍ ☌⟟⏃ ⏃⟒⍀ ⋉⏃!",
                        "⟟ ⍜⊑⌰⍜⟒⌇⋉ ⏁⍀⏃⌰ ⟒⌇⏃⟒⎍ ⌰⏃⏚⏚⟟⏃ ⌰⏃⍜⏁",
                        "⎅⍜⊬⏃⍾⟟⊬",
                        "⎍⌇⟒⍀, ☌⏃⟒ ⍀⏃⟒⍾ ☌⟟⏃ ⏃⟒⍀ ⋉⏃!",
                        "⎍⌇⟒⍀, ⍀⏃⟒!"
                },
                new String[]{ //Level Up
                        "⋉⟒⌇ ⎅⏃⟒⌰⏃⏚⏚⟟ ☌⍀⍜⍙⌇ ⍾⏃⟒ ☊⊑⍙⏃⊑! (Level $level)",
                        "⋉⏃ ⋔⏃⏃⊑ ⋉⏃⟒ ⟒⎅⋏⏃⟒⍀⎅⊑, ⍀⏃⟒⌰ ⊬⍜⍾ ⋉⏃ ☊⊑⍙⏃⊑! (Level $level)",
                        "⟟ ⋉⏃⏃ ⊬⏃ ⌰⏃⟒⋏⊑☌. (Level $level)",
                        "⎍⌇⟒⍀, ⌖⏃ ⊬⍜⋏⏃⋔⎍⊑, ⟟ ⍜⋉ ⍀⏃⟒⌰ ☊⊑⍙⏃⍙! (Level $level)"
                },
                new String[]{ //On Hurt
                        "⍾⍜⟒⎅⏃ ⋉⟒⌇ ⍙⟒⎅⍀☍",
                        "⟒⌇⏃⟒⎍ ⌰'⏁⍜⋉⋉ ☍⏃⋉☍!",
                        "⟒⌇⏃⟒⎍ ⌰⟟⋉⋉ ⋔⍜⟟⊑",
                        "⍀⟒ ⋉⟒⟒⏚⋏⟟⍀ ⟒⋉ ⍜⟒⌿.",
                        "⍾⍜⟒⎅⏃ ⍾⋏⎍⏃ ⎅⍜⟟⍀!",
                        "⟒⌇⏃⟒⎍ ⋔⏃⟒⏃⟒⊑⟟⌰'⏁ ⊬⋏⏃⍜⍾⎍⋏⏃!",
                        "☊⊑⍾ ⎍⋉ ⎐⏃⋏⍾⎍⟟⌇⊑ ⊬⟟⋉ ⎎⍜⟒, ⎍⌇⟒⍀!",
                        "⎍⌇⟒⍀, ⊬⟟⋉ ⊬⋏⏃⍜⍾⎍⋏⏃ ☌⏃⟒⏃⋉ ⍀⏃⟒⍾ ⏁⍀⏃⟒⌰ ⏃⟒⎍⋏ ⟒⎅⋏⏃⍀⎅⊑⊬! ☊⊑⍾ ⎍⋉ ⌰'⏁⏃⟒⌰ ⟟⍾!",
                        "⟒⌇⏃⟒⎍ ⌰⟟⋉⋉ ⏁⍀⏃⟒⌰ ⊑⏃⟒⋉⏃",
                        "⎍⌇⟒⍀, ⊬⟟⋉ ⊬⋏⏃⍜⍾⎍⋏⏃ ⋉⍜⟒⌇ ⍀⏃⟒⍾ ⌖⏃ ⏃⌰⌰⍜⍙⟒⎅ ⍾⏃⟒ ⏃⌿⟟⟒⎅!",
                },
                12,
                new String[]{ //Twenty percent
                        "⋔⊬ ⌰⟟⎎⟒ ⟒⌇⌇⟒⋏☊⟒ ⟟⌇ ☌⟒⏁⏁⟟⋏☌ ⎎⏃⟟⍀⌰⊬ ⌰⍜⍙... ($durability durability remaining)",
                        "⟟⌇⋏'⏁ ⟟⏁ ⏁⟟⋔⟒ ⊬⍜⎍ ⍀⟒⌿⏃⟟⍀⟒⎅ ⋔⟒? ($durability durability remaining)",
                        "⟟'⋔ ⎎⟒⟒⌰⟟⋏☌ ⌿⍀⟒⏁⏁⊬ ⍙⟒⏃☍ ($durability durability remaining)",
                },
                new String[]{ //Five percent
                        "⋔⊬ ⌰⟟⎎⟒ ⟒⌇⌇⟒⋏☊⟒ ⟟⌇ ☌⟒⏁⏁⟟⋏☌ ⎎⏃⟟⍀⌰⊬ ⌰⍜⍙... ($durability durability remaining!)",
                        "⟟⌇⋏'⏁ ⟟⏁ ⏁⟟⋔⟒ ⊬⍜⎍ ⍀⟒⌿⏃⟟⍀⟒⎅ ⋔⟒? ($durability durability remaining!)",
                        "⟟'⋔ ⎎⟒⟒⌰⟟⋏☌ ⌿⍀⟒⏁⏁⊬ ⍙⟒⏃☍ ($durability durability remaining!)",
                }));
        createPersonalityFile(new Personality(10, "Energetic",
                new String[]{ //Use
                        "Again! Again!",
                        "This is so fun, let's keep doing it, $user!",
                        "Keep going!",
                        "Yay!",
                        "I could do this all day!",
                        "$user, don't stop! This is so much fun!",
                        "I want more!",
                        "This is exciting! Don't you agree, $user?"
                },
                24,
                new String[]{ //Kill
                        "We really showed them!",
                        "Good job, $user!",
                        "Killing is so much work! I love it!"
                },
                12,
                new String[]{ //Death
                        "Oh no! Is this the end?",
                        "Noooo! We were only getting started!",
                        "We'll get them next time, right? $user? ...oh, you died.",
                        "Don't forget about me, $user!"
                },
                new String[]{ //Level Up
                        "Ah yes, more power! (Level $level)",
                        "I can feel the power flowing through me, $user! Let's keep going! (Level $level)",
                        "I feel stronger already, $user! (Level $level)!"
                },
                new String[]{ //On Hurt
                        "Ouch! I bet they'll feel that one tomorrow, $user!",
                        "Ooo, that looks like it had to hurt!",
                        "Ouch!",
                        "Take that!"
                },
                16,
                new String[]{ //Twenty percent
                        "I'm starting to hurt, $user. ($durability durability remaining)",
                        "I'm feeling weaker, $user. ($durability durability remaining)",
                        "Don't forget about me, $user! ($durability durability remaining)"
                },
                new String[]{ //Five percent
                        "(oof...) Is... is this the end of our adventure, $user? ($durability durability remaining!)",
                        "I don't feel so good, $user... ($durability durability remaining!)",
                        "I don't wanna die, $user! ($durability durability remaining!)",
                        "I guess... this is it, huh $user? ($durability durability remaining!)"
                }));
    }

    public static Personality loadPersonality(File personalityLocation) {
        float useOdds = 50, killOdds = 50, hurtOdds = 50, weight = 50;
        String name = "???";
        ArrayList<String> onUse = new ArrayList<>();
        ArrayList<String> onKill = new ArrayList<>();
        ArrayList<String> onDeath = new ArrayList<>();
        ArrayList<String> onLevelUp = new ArrayList<>();
        ArrayList<String> onHurt = new ArrayList<>();
        ArrayList<String> twentyPercent = new ArrayList<>();
        ArrayList<String> fivePercent = new ArrayList<>();
        String state = "none";
        String line = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(personalityLocation), StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().startsWith("[name]")) {
                    state = "name";
                    continue;
                } else if (line.toLowerCase().startsWith("[weight]")) {
                    state = "weight";
                    continue;
                } else if (line.toLowerCase().startsWith("[use]")) {
                    state = "use";
                    continue;
                } else if (line.toLowerCase().startsWith("[useweight]")) {
                    state = "useweight";
                    continue;
                } else if (line.toLowerCase().startsWith("[kill]")) {
                    state = "kill";
                    continue;
                } else if (line.toLowerCase().startsWith("[killweight]")) {
                    state = "killweight";
                    continue;
                } else if (line.toLowerCase().startsWith("[death]")) {
                    state = "death";
                    continue;
                } else if (line.toLowerCase().startsWith("[levelup]")) {
                    state = "levelup";
                    continue;
                } else if (line.toLowerCase().startsWith("[hurt]")) {
                    state = "hurt";
                    continue;
                } else if (line.toLowerCase().startsWith("[hurtweight]")) {
                    state = "hurtweight";
                    continue;
                } else if (line.toLowerCase().startsWith("[25%durability]")) {
                    state = "25%durability";
                    continue;
                } else if (line.toLowerCase().startsWith("[10%durability]")) {
                    state = "10%durability";
                    continue;
                }
                try {
                    switch (state) {
                        case "name":
                            name = line;
                            break;
                        case "weight":
                            weight = Float.parseFloat(line);
                            break;
                        case "use":
                            onUse.add(line);
                            break;
                        case "useweight":
                            useOdds = Float.parseFloat(line);
                            break;
                        case "kill":
                            onKill.add(line);
                            break;
                        case "killweight":
                            killOdds = Float.parseFloat(line);
                            break;
                        case "death":
                            onDeath.add(line);
                            break;
                        case "levelup":
                            onLevelUp.add(line);
                            break;
                        case "hurt":
                            onHurt.add(line);
                            break;
                        case "hurtweight":
                            hurtOdds = Float.parseFloat(line);
                            break;
                        case "25%durability":
                            twentyPercent.add(line);
                            break;
                        case "10%durability":
                            fivePercent.add(line);
                            break;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Personality(weight, name, onUse.toArray(new String[onUse.size()]), (int) useOdds, onKill.toArray(new String[onKill.size()]), (int) killOdds, onDeath.toArray(new String[onDeath.size()]), onLevelUp.toArray(new String[onLevelUp.size()]), onHurt.toArray(new String[onHurt.size()]), (int) hurtOdds, twentyPercent.toArray(new String[twentyPercent.size()]), fivePercent.toArray(new String[fivePercent.size()]));
    }
}
