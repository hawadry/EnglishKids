package com.example.data.curriculum

import androidx.compose.ui.graphics.Color
import com.example.data.model.CurriculumCategory
import com.example.data.model.LearningWord
import com.example.ui.theme.BerryPurple
import com.example.ui.theme.BerryPurpleContainer
import com.example.ui.theme.CoralPink
import com.example.ui.theme.CoralPinkContainer
import com.example.ui.theme.MeadowGreen
import com.example.ui.theme.MeadowGreenContainer
import com.example.ui.theme.SkyTurquoise
import com.example.ui.theme.SkyTurquoiseContainer
import com.example.ui.theme.StarGold
import com.example.ui.theme.StarGoldContainer
import com.example.ui.theme.SunnyOrange
import com.example.ui.theme.SunnyOrangeContainer

object EnglishCurriculum {

    val categories: List<CurriculumCategory> = listOf(
        CurriculumCategory(
            id = "alphabet",
            titleAr = "الحروف الإنجليزية",
            titleEn = "Alphabet A–Z",
            subtitleAr = "تعلم نطق الحروف الـ 26 مع كلمة وصورة",
            emoji = "🔤",
            accentColor = SunnyOrange,
            containerColor = SunnyOrangeContainer
        ),
        CurriculumCategory(
            id = "numbers",
            titleAr = "الأرقام والعد",
            titleEn = "Numbers 1–12",
            subtitleAr = "عدّ النجوم وتعلم الأرقام بالإنجليزية",
            emoji = "🔢",
            accentColor = SkyTurquoise,
            containerColor = SkyTurquoiseContainer
        ),
        CurriculumCategory(
            id = "animals",
            titleAr = "عالم الحيوانات",
            titleEn = "Animals",
            subtitleAr = "أسماء الحيوانات اللطيفة وأصواتها",
            emoji = "🦁",
            accentColor = MeadowGreen,
            containerColor = MeadowGreenContainer
        ),
        CurriculumCategory(
            id = "colors",
            titleAr = "الألوان والأشكال",
            titleEn = "Colors & Shapes",
            subtitleAr = "ألوان قوس قزح والأشكال المرحة",
            emoji = "🎨",
            accentColor = BerryPurple,
            containerColor = BerryPurpleContainer
        ),
        CurriculumCategory(
            id = "food",
            titleAr = "الفواكه والطعام",
            titleEn = "Fruits & Food",
            subtitleAr = "فواكه لذيذة وأطعمة صحية كل يوم",
            emoji = "🍓",
            accentColor = CoralPink,
            containerColor = CoralPinkContainer
        ),
        CurriculumCategory(
            id = "greetings",
            titleAr = "التحيات والعائلة",
            titleEn = "Family & Greetings",
            subtitleAr = "كلمات الترحيب وأفراد الأسرة",
            emoji = "👋",
            accentColor = StarGold,
            containerColor = StarGoldContainer
        )
    )

    val words: List<LearningWord> = listOf(
        // 1. Alphabet A-Z (26 items)
        LearningWord("alpha_a", "alphabet", "A a", "Apple", "تفاحة", "آبل", "🍎", "A is for Apple.", "حرف A لكلمة تفاحة."),
        LearningWord("alpha_b", "alphabet", "B b", "Ball", "كرة", "بول", "⚽", "B is for Ball.", "حرف B لكلمة كرة."),
        LearningWord("alpha_c", "alphabet", "C c", "Cat", "قطة", "كات", "🐱", "C is for Cat.", "حرف C لكلمة قطة."),
        LearningWord("alpha_d", "alphabet", "D d", "Duck", "بطة", "داك", "🦆", "D is for Duck.", "حرف D لكلمة بطة."),
        LearningWord("alpha_e", "alphabet", "E e", "Elephant", "فيل", "إليفانت", "🐘", "E is for Elephant.", "حرف E لكلمة فيل."),
        LearningWord("alpha_f", "alphabet", "F f", "Fish", "سمكة", "فيش", "🐠", "F is for Fish.", "حرف F لكلمة سمكة."),
        LearningWord("alpha_g", "alphabet", "G g", "Giraffe", "زرافة", "جيراف", "🦒", "G is for Giraffe.", "حرف G لكلمة زرافة."),
        LearningWord("alpha_h", "alphabet", "H h", "House", "بيت", "هاوس", "🏠", "H is for House.", "حرف H لكلمة بيت."),
        LearningWord("alpha_i", "alphabet", "I i", "Ice Cream", "مثلجات", "آيس كريم", "🍦", "I is for Ice Cream.", "حرف I لكلمة مثلجات."),
        LearningWord("alpha_j", "alphabet", "J j", "Juice", "عصير", "جوس", "🧃", "J is for Juice.", "حرف J لكلمة عصير."),
        LearningWord("alpha_k", "alphabet", "K k", "Kite", "طائرة ورقية", "كايت", "🪁", "K is for Kite.", "حرف K لكلمة طائرة ورقية."),
        LearningWord("alpha_l", "alphabet", "L l", "Lion", "أسد", "لايون", "🦁", "L is for Lion.", "حرف L لكلمة أسد."),
        LearningWord("alpha_m", "alphabet", "M m", "Moon", "قمر", "مون", "🌙", "M is for Moon.", "حرف M لكلمة قمر."),
        LearningWord("alpha_n", "alphabet", "N n", "Nest", "عش", "نيست", "🪺", "N is for Nest.", "حرف N لكلمة عش."),
        LearningWord("alpha_o", "alphabet", "O o", "Orange", "برتقالة", "أورنج", "🍊", "O is for Orange.", "حرف O لكلمة برتقالة."),
        LearningWord("alpha_p", "alphabet", "P p", "Pen", "قلم", "بن", "🖊️", "P is for Pen.", "حرف P لكلمة قلم."),
        LearningWord("alpha_q", "alphabet", "Q q", "Queen", "ملكة", "كوين", "👑", "Q is for Queen.", "حرف Q لكلمة ملكة."),
        LearningWord("alpha_r", "alphabet", "R r", "Rabbit", "أرنب", "رابيت", "🐰", "R is for Rabbit.", "حرف R لكلمة أرنب."),
        LearningWord("alpha_s", "alphabet", "S s", "Sun", "شمس", "صن", "☀️", "S is for Sun.", "حرف S لكلمة شمس."),
        LearningWord("alpha_t", "alphabet", "T t", "Tree", "شجرة", "تري", "🌳", "T is for Tree.", "حرف T لكلمة شجرة."),
        LearningWord("alpha_u", "alphabet", "U u", "Umbrella", "مظلة", "أمبريلا", "☂️", "U is for Umbrella.", "حرف U لكلمة مظلة."),
        LearningWord("alpha_v", "alphabet", "V v", "Van", "شاحنة صغيرة", "فان", "🚐", "V is for Van.", "حرف V لكلمة شاحنة."),
        LearningWord("alpha_w", "alphabet", "W w", "Whale", "حوت", "ويل", "🐳", "W is for Whale.", "حرف W لكلمة حوت."),
        LearningWord("alpha_x", "alphabet", "X x", "Xylophone", "إكسيليفون", "زايلوفون", "🎼", "X is for Xylophone.", "حرف X لآلة الإكسيليفون."),
        LearningWord("alpha_y", "alphabet", "Y y", "Yo-yo", "لعبة يويو", "يويو", "🪀", "Y is for Yo-yo.", "حرف Y للعبة اليويو."),
        LearningWord("alpha_z", "alphabet", "Z z", "Zebra", "حمار وحشي", "زيبرا", "🦓", "Z is for Zebra.", "حرف Z لكلمة حمار وحشي."),

        // 2. Numbers 1-12
        LearningWord("num_1", "numbers", "1", "One", "واحد", "وَن", "1️⃣", "I have one sun.", "لدي شمس واحدة.", countNumber = 1),
        LearningWord("num_2", "numbers", "2", "Two", "اثنان", "تو", "2️⃣", "Two little birds.", "عصفوران صغيران.", countNumber = 2),
        LearningWord("num_3", "numbers", "3", "Three", "ثلاثة", "ثري", "3️⃣", "Three bright stars.", "ثلاث نجوم لامعة.", countNumber = 3),
        LearningWord("num_4", "numbers", "4", "Four", "أربعة", "فور", "4️⃣", "Four green apples.", "أربع تفاحات خضراء.", countNumber = 4),
        LearningWord("num_5", "numbers", "5", "Five", "خمسة", "فايف", "5️⃣", "Five fingers on my hand.", "خمس أصابع في يدي.", countNumber = 5),
        LearningWord("num_6", "numbers", "6", "Six", "ستة", "سيكس", "6️⃣", "Six colorful balloons.", "ست بالونات ملونة.", countNumber = 6),
        LearningWord("num_7", "numbers", "7", "Seven", "سبعة", "سيفن", "7️⃣", "Seven days in a week.", "سبعة أيام في الأسبوع.", countNumber = 7),
        LearningWord("num_8", "numbers", "8", "Eight", "ثمانية", "إيت", "8️⃣", "Eight legs on an octopus.", "ثماني أرجل للأخطبوط.", countNumber = 8),
        LearningWord("num_9", "numbers", "9", "Nine", "تسعة", "ناين", "9️⃣", "Nine happy clouds.", "تسع سحابات سعيدة.", countNumber = 9),
        LearningWord("num_10", "numbers", "10", "Ten", "عشرة", "تين", "🔟", "Ten shining stars!", "عشر نجوم متألقة!", countNumber = 10),
        LearningWord("num_11", "numbers", "11", "Eleven", "أحد عشر", "إليفن", "⭐", "Eleven soccer players.", "أحد عشر لاعب كرة.", countNumber = 11),
        LearningWord("num_12", "numbers", "12", "Twelve", "اثنا عشر", "تويلف", "🕛", "Twelve months in a year.", "اثنا عشر شهراً في السنة.", countNumber = 12),

        // 3. Animals
        LearningWord("animal_lion", "animals", "Lion", "Lion", "أسد", "لايون", "🦁", "The lion is strong.", "الأسد قوي."),
        LearningWord("animal_cat", "animals", "Cat", "Cat", "قطة", "كات", "🐱", "The cat drinks milk.", "القطة تشرب الحليب."),
        LearningWord("animal_dog", "animals", "Dog", "Dog", "كلب", "دوج", "🐶", "The dog is friendly.", "الكلب ودود."),
        LearningWord("animal_bird", "animals", "Bird", "Bird", "طائر", "بيرد", "🐦", "The bird can fly.", "الطائر يستطيع الطيران."),
        LearningWord("animal_fish", "animals", "Fish", "Fish", "سمكة", "فيش", "🐠", "The fish swims in water.", "السمكة تسبح في الماء."),
        LearningWord("animal_rabbit", "animals", "Rabbit", "Rabbit", "أرنب", "رابيت", "🐰", "The rabbit loves carrots.", "الأرنب يحب الجزر."),
        LearningWord("animal_monkey", "animals", "Monkey", "Monkey", "قرد", "مانكي", "🐵", "The monkey eats a banana.", "القرد يأكل موزة."),
        LearningWord("animal_duck", "animals", "Duck", "Duck", "بطة", "داك", "🦆", "The duck swims on the pond.", "البطة تسبح في البحيرة."),
        LearningWord("animal_turtle", "animals", "Turtle", "Turtle", "سلحفاة", "تيرتل", "🐢", "The turtle walks slowly.", "السلحفاة تمشي ببطء."),
        LearningWord("animal_butterfly", "animals", "Butterfly", "Butterfly", "فراشة", "باترفلاي", "🦋", "Look at the pretty butterfly!", "انظر إلى الفراشة الجميلة!"),
        LearningWord("animal_elephant", "animals", "Elephant", "Elephant", "فيل", "إليفانت", "🐘", "The elephant has a long trunk.", "الفيل له خرطوم طويل."),
        LearningWord("animal_giraffe", "animals", "Giraffe", "Giraffe", "زرافة", "جيراف", "🦒", "The giraffe is very tall.", "الزرافة طويلة جداً."),

        // 4. Colors & Shapes
        LearningWord("color_red", "colors", "Red", "Red", "أحمر", "ريد", "🔴", "The apple is red.", "التفاحة حمراء.", swatchColor = Color(0xFFE53935)),
        LearningWord("color_blue", "colors", "Blue", "Blue", "أزرق", "بلو", "🔵", "The sky is blue.", "السماء زرقاء.", swatchColor = Color(0xFF1E88E5)),
        LearningWord("color_green", "colors", "Green", "Green", "أخضر", "جرين", "🟢", "The grass is green.", "العشب أخضر.", swatchColor = Color(0xFF43A047)),
        LearningWord("color_yellow", "colors", "Yellow", "Yellow", "أصفر", "يلو", "🟡", "The sun is yellow.", "الشمس صفراء.", swatchColor = Color(0xFFFDD835)),
        LearningWord("color_orange", "colors", "Orange", "Orange", "برتقالي", "أورنج", "🟠", "An orange carrot.", "جزرة برتقالية.", swatchColor = Color(0xFFFB8C00)),
        LearningWord("color_purple", "colors", "Purple", "Purple", "بنفسجي", "بيربل", "🟣", "I like purple grapes.", "أحب العنب البنفسجي.", swatchColor = Color(0xFF8E24AA)),
        LearningWord("color_pink", "colors", "Pink", "Pink", "وردي", "بينك", "🩷", "A cute pink flower.", "زهرة وردية جميلة.", swatchColor = Color(0xFFEC407A)),
        LearningWord("shape_circle", "colors", "Circle", "Circle", "دائرة", "سيركل", "⭕", "The wheel is a circle.", "العجلة شكلها دائرة."),
        LearningWord("shape_star", "colors", "Star", "Star", "نجمة", "ستار", "⭐", "Twinkle little star.", "تألقي يا نجمة صغيرة."),
        LearningWord("shape_heart", "colors", "Heart", "Heart", "قلب", "هارت", "❤️", "A big red heart.", "قلب أحمر كبير."),

        // 5. Fruits & Food
        LearningWord("food_apple", "food", "Apple", "Apple", "تفاحة", "آبل", "🍎", "I eat a fresh apple.", "آكل تفاحة طازجة."),
        LearningWord("food_banana", "food", "Banana", "Banana", "موزة", "بانانا", "🍌", "Bananas are sweet.", "الموز حلو المذاق."),
        LearningWord("food_orange", "food", "Orange", "Orange", "برتقالة", "أورنج", "🍊", "Fresh orange juice.", "عصير برتقال طازج."),
        LearningWord("food_strawberry", "food", "Strawberry", "Strawberry", "فراولة", "ستروبيري", "🍓", "Sweet red strawberry.", "فراولة حمراء حلوة."),
        LearningWord("food_grapes", "food", "Grapes", "Grapes", "عنب", "جريبس", "🍇", "I share the grapes.", "أشارك العنب مع أصدقائي."),
        LearningWord("food_watermelon", "food", "Watermelon", "Watermelon", "بطيخ", "وترميلون", "🍉", "Cool watermelon in summer.", "بطيخ بارد في الصيف."),
        LearningWord("food_milk", "food", "Milk", "Milk", "حليب", "ميلك", "🥛", "Drink milk every morning.", "اشرب الحليب كل صباح."),
        LearningWord("food_bread", "food", "Bread", "Bread", "خبز", "بريد", "🍞", "Warm fresh bread.", "خبز طازج دافئ."),
        LearningWord("food_egg", "food", "Egg", "Egg", "بيضة", "إيج", "🥚", "A healthy breakfast egg.", "بيضة فطور صحية."),
        LearningWord("food_honey", "food", "Honey", "Honey", "عسل", "هاني", "🍯", "Golden sweet honey.", "عسل ذهبي حلو."),

        // 6. Family & Greetings
        LearningWord("greet_hello", "greetings", "Hello", "Hello", "مرحباً", "هيلو", "👋", "Hello, my friend!", "مرحباً يا صديقي!"),
        LearningWord("greet_morning", "greetings", "Good morning", "Good morning", "صباح الخير", "جود مورنينج", "🌅", "Good morning, Mom!", "صباح الخير يا أمي!"),
        LearningWord("greet_thanks", "greetings", "Thank you", "Thank you", "شكراً لك", "ثانك يو", "🙏", "Thank you very much!", "شكراً جزيلاً لك!"),
        LearningWord("greet_please", "greetings", "Please", "Please", "من فضلك", "بليز", "😊", "Water, please.", "ماء، من فضلك."),
        LearningWord("fam_mother", "greetings", "Mother", "Mother", "أم", "ماذر", "👩", "I love my mother.", "أنا أحب أمي."),
        LearningWord("fam_father", "greetings", "Father", "Father", "أب", "فاذر", "👨", "My father reads with me.", "أبي يقرأ معي."),
        LearningWord("fam_sister", "greetings", "Sister", "Sister", "أخت", "سيستر", "👧", "My sister is kind.", "أختي لطيفة."),
        LearningWord("fam_brother", "greetings", "Brother", "Brother", "أخ", "براذر", "👦", "I play with my brother.", "ألعب مع أخي."),
        LearningWord("greet_water", "greetings", "Water", "Water", "ماء", "ووتر", "💧", "Drink clean water.", "اشرب ماءً نظيفاً."),
        LearningWord("greet_school", "greetings", "School", "School", "مدرسة", "سكول", "🏫", "I love my school.", "أنا أحب مدرستي.")
    )

    fun categoryById(id: String): CurriculumCategory {
        return categories.firstOrNull { it.id == id } ?: categories.first()
    }

    fun wordsForCategory(categoryId: String): List<LearningWord> {
        return if (categoryId == "mixed") words else words.filter { it.categoryId == categoryId }
    }

    fun spellingEligibleWords(categoryId: String): List<LearningWord> {
        return wordsForCategory(categoryId).filter { word ->
            val clean = word.englishWord.uppercase()
            clean.all { it in 'A'..'Z' } && clean.length in 3..8
        }
    }
}
