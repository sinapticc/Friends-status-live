# Character art replacement — live spec

Date: 2026-09-25. Owner requirement: replace the SVG-derived characters with attractive art that communicates the status immediately, without relying on text. Current work priority is these images. Update `docs/HANDOFF.md` after each meaningful batch.

## Art direction

- Use the built-in imagegen tool. **Each status must have its own distinct character**, not the same mascot in a different pose, color, costume or prop. The owner's correction on 2026-09-25 supersedes the initial shared-mascot concept. Keep only a common level of finish: hand-sculpted tactile 3D, matte plush/clay material, controlled palette and lighting.
- Prefer making the **status symbol itself the character** where possible. The owner's explicit example: `toilet` must be a cute poop character, **not** a character using a toilet. Avoid literal scenes of another character performing an action when an instantly legible symbolic character works better.
- `source/character-art/ch_sleeping.png` (1254 square, transparent PNG) is the accepted concept for the sleeping status only. Its cream sprout creature must not appear in any other status. Former gym/free/eating/studying shared-mascot attempts are in `archive/attempt-02/character-art/` and must not be packaged.
- ONE isolated character or pair per image, genuine alpha transparency, centered square framing, generous margins, no background, card, typography, logo, watermark or decorative confetti. Props must be large enough to read at 44 dp. Do not use location/map imagery.
- Status artwork is consumed as `ch_<key>` in `StatusChar` and widgets. Existing `drawable-nodpi/ch_*.webp` files are baseline; keep PNG masters in `source/character-art/` and package verified WebP replacements with the same resource names. Preserve `design/` historical files. `me_*` layers and `acc_*` overlays are also SVG-derived; finish the 44 status artworks first, then adapt the character editor/overlays without mixing visibly different styles.
- Each imagegen call produces one asset. Prefer no reference image for a new status to prevent identity leakage. Define a unique species/object, silhouette, colors, face and pose along with the obvious status prop. Gym headband/wristbands belong only in gym artwork; the owner explicitly rejected them in sleeping artwork. Inspect both full resolution and ~64 px thumbnail before replacing old art. Reject repeated character identities, extra limbs, ambiguous props, white matte, clipped edges or opaque background.

## Prompt scaffold

Use case: stylized-concept. Asset type: transparent square character sticker for Persian friends-status Android app, shown at 44–210 dp. Design a unique character for `<key>`: `<distinct form, silhouette, colors, facial identity>`. It must be visibly different from every other status character, especially the cream sleeping sprout creature. Show `<one clear pose/prop>`, readable without text. Premium handcrafted 3D clay/plush material, restrained palette, softly lit. Center the complete character with generous margins and genuine transparent alpha. No unrelated status props, scene, floor, card, words, logo, watermark, generic glow, extra limbs or apparel unless relevant.

## Unambiguous status cues

| Key | Persian label | Primary visual cue |
|---|---|---|
| toilet | دستشویی‌ام | Cute poop-swirl character itself, unmistakable silhouette; no toilet or seated person |
| sleeping | خوابم | Curled under small blanket with pillow, closed eyes — corrected master complete, no sports gear |
| eating | دارم غذا می‌خورم | Fork lifting noodles from bowl |
| gym | باشگاهم | Lifting two dumbbells — new distinct character needed |
| studying | دارم درس می‌خونم | Open textbook, pencil, studying expression |
| work | سر کارم | Laptop with hands typing, small desk |
| driving | پشت فرمونم | Gripping a large visible steering wheel inside a car silhouette |
| gaming | دارم گیم می‌زنم | Holding unmistakable game controller |
| partying | مهمونی‌ام | Dance pose with party cone and a few paper streamers |
| showering | حمومم | Shower head above, water droplets, towel |
| coffee | وقت قهوه‌ست | Hugging a large steaming coffee cup |
| sick | مریضم | Thermometer, blanket and tissue |
| walking | دارم قدم می‌زنم | Mid-step in walking shoes with tiny motion marks |
| dnd | مزاحم نشید | Sleep mask and raised stop palm; no bell text |
| bored | حوصله‌م سر رفته | Slumped on beanbag with long droopy face |
| free | بیکارم · پایه‌ام | Open welcoming arms, no tools, cheerful ready pose |
| custom | دلخواه | Holding paint brush and blank color swatch |
| period | پریودم | Holding red hot-water bottle against abdomen; discreet |
| spicy | یه کم داغم | Blushing face with tiny warm-red heat wave |
| spicy2 | آتیشی‌ام | Stronger heat, small flame symbol held safely |
| spicy3 | کمک! | Alarmed expression waving for help, hot-water bottle nearby |
| inlove | عاشقم | Hugging a single oversized red heart |
| heartbroken | دلم شکسته | Holding visibly cracked heart |
| hungover | خمارم | Ice pack on head, water glass, exhausted eyes |
| angry | عصبانی‌ام | Furrowed brow, arms crossed, small steam puff |
| crying | دارم گریه می‌کنم | Big distinct tear drops and tissue |
| date | سر قرارم | Two matching mascots at tiny candle table, shy smiles |
| shopping | دارم خرید می‌کنم | Carrying two bold shopping bags |
| movie | دارم فیلم می‌بینم | Popcorn bowl and movie clapper/film reel |
| traveling | سفرم | Small suitcase, boarding pass shape, no readable text |
| cooking | دارم آشپزی می‌کنم | Stirring food in a saucepan with chef apron |
| meditating | مدیتیشن | Calm cross-legged pose, closed eyes |
| hookah | قلیون | Holding recognizable hookah hose next to compact hookah |
| cleaning | دارم خونه تکونی می‌کنم | Broom sweeping a small dust pile |
| traffic | تو ترافیکم | At steering wheel with red traffic light and queue silhouettes |
| lowbattery | شارژم کمه | Slumped next to battery outline almost empty and charging cable |
| overthinking | زیادی فکر می‌کنم | Hands at head, clear tangled thought loops overhead |
| maincharacter | نقش اولم | Spotlight and cinematic clapperboard, confident pose |
| busy | سرم شلوغه | Multiple stacked folders in arms and clock, overwhelmed |
| period2 | حالت شکلاتی | Hot-water bottle plus large chocolate bar |
| period3 | باهام حرف نزن | Hot-water bottle, crossed arms and stern face |
| football | دارم فوتبال می‌بینم | Watching TV with large soccer ball on screen, snack bowl |
| barber | آرایشگاهم | Seated under barber cape as scissors trim hair |
| beard | اصلاح ریش | Mirror and clearly visible small beard/razor |

## Distinct character identities

These are starting identities, not a reason to keep an unclear drawing. The unique body shape must remain obvious at thumbnail size; accessory-only or color-only changes are insufficient.

| Key | Character identity |
|---|---|
| toilet | Cocoa-brown poop-swirl creature with a distinctive three-tier curl and face |
| sleeping | Cream oval sprout creature with two green leaf ears — completed |
| eating | Coral red dumpling creature with pleated top |
| gym | Squat lilac bear cub with broad paws |
| studying | Midnight blue owl with ear tufts and round spectacles |
| work | Navy office-briefcase creature holding a blank document |
| driving | Cherry-red car creature with its own hands at the wheel |
| gaming | Turquoise game-controller creature with its own face and hands |
| partying | Gold striped party-hat creature with pompom and streamers |
| showering | Sky-blue shower-head creature spraying visible drops |
| coffee | Ivory espresso-cup creature with steam and handle |
| sick | Pale mint thermometer creature with tissue and lavender blanket |
| walking | Sunny yellow lace-up sneaker creature in mid-stride |
| dnd | Deep-plum muted-bell creature with coral slash and stop hand |
| bored | Slate gray sloth with long drooping arms |
| free | Orange starfish with five expressive limbs |
| custom | Birch-wood paint-palette creature with bright color dabs and brush |
| period | Dusty-rose hot-water-bottle creature with small droplet motif |
| spicy | Small orange chili creature with curved stem |
| spicy2 | Crimson baby dragon with tiny wings |
| spicy3 | Red crab with large startled claws |
| inlove | Raspberry heart-shaped creature with rounded feet |
| heartbroken | Blue ceramic heart creature with visible crack |
| hungover | Wilted lavender flower with drooping petals |
| angry | Brick red bull with small curved horns |
| crying | Sky blue raindrop with teardrop silhouette |
| date | Two distinct tiny birds, one teal and one peach, sharing a table |
| shopping | Teal raccoon with striped tail |
| movie | Butter-yellow popcorn tub creature with popcorn crown |
| traveling | Turquoise suitcase creature with handle-shaped head |
| cooking | Copper orange otter with whiskers and long tail |
| meditating | Pale jade lotus creature with layered petal body |
| hookah | Indigo genie with curled smoke tail |
| cleaning | Lemon yellow broom creature with bristle skirt |
| traffic | Olive snail with spiral shell at steering wheel |
| lowbattery | Nearly empty charcoal battery creature with tiny red charge bar |
| overthinking | Violet jellyfish with trailing curling tentacles |
| maincharacter | Gold star headed stage actor |
| busy | Blue-gray clockwork octopus with many folders |
| period2 | Chocolate-bar creature partly wrapped in foil, hugging a hot-water bottle |
| period3 | Magenta porcupine with stiff upright quills |
| football | Black-and-white soccer ball creature with stubby legs |
| barber | Fluffy ivory sheep with curly fleece |
| beard | Cinnamon walrus with prominent whiskers |
