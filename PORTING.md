# Ae2EmiQuickFill — порт на Minecraft 26.1.2 / NeoForge 26.1.2.126

Порт форка MehanikTMYT/Ae2EmiQuickFill, ветка `1.21.1`, коммит `a3d71c3` («1.0.1：样板编码终端也能
Shift+点击拉配方»), MIT. Мод крошечный: 1 класс-точка входа + 1 миксин, ~917 строк.
Суть: Shift-клик по рецепту в EMI отправляет его в терминал AE2 (синтез, беспроводной синтез,
кодировщик образцов).

Ветка: `port/26.1.2`. Целевые версии: Minecraft **26.1.2**, NeoForge **26.1.2.126**, **Java 25**,
Gradle **9.2.1**, moddev **2.0.141**.

## Что поменялось при порте

### Сборка

- `gradle-wrapper` 8.8 → **9.2.1** (обёртка скопирована с эталонного
  `worktrees/ends-delight-26.1.2`).
- moddev `2.0.74` → **2.0.141**: 2.0.74 не проверен на 26.1.2; 2.0.141 — та же версия, что в
  собранном флотом `AE-Compendium` (та же экосистема AE2+EMI на 26.1.2 + Gradle 9.2.1).
- `neo_version` `21.1.251` → **26.1.2.126**, `java_version` 21 → **25**
  (`options.release = 25`, toolchain 25).
- Зависимости переведены с ручных jar/maven на **mavenLocal флота**:
  - AE2: `org.appliedenergistics:appliedenergistics2:26.1.12-SNAPSHOT` (`compileOnly`), вместо
    `19.2.17` с modmaven. Транзитивный `org.appliedenergistics:guideme:26.1.14-beta` резолвится
    через modmaven (jar уже в кэше gradle).
  - EMI: `dev.emi:emi-neoforge:1.1.24-semi151a6c88+26.1.2` из mavenLocal — `:api` классификатор в
    `compileOnly`, полный jar в `localRuntime` (для дев-запусков). Каталог `libs/` больше не нужен;
    `libs/README.md` переписан.
- В `processResources` добавлены `mod_license` и `_range`-переменные (.mods.toml теперь
  использует `${neo_version_range}` / `${minecraft_version_range}` / `${loader_version_range}`).
- `org.gradle.daemon=false` — как в эталоне ends-delight (не плодим демоны в общем окружении).

### Ресурсы

- `pack.mcmeta`: `pack_format: 34` → `min_format: 84` / `max_format: [101, 1]` (формат 26.1.2,
  как в QA-проверенном порте ends-delight).
- `neoforge.mods.toml`:
  - зависимости `neoforge` → `[26.1.2.122,26.2)`, `minecraft` → `[26.1.2,26.2)`,
    `ae2` → `[26.1,)` (26.1.11/26.1.12-SNAPSHOT проходят), `emi` → `[1.1.18,)` без изменений;
  - `[[mixins]]` и `displayTest = "IGNORE_ALL_VERSION"` оставлены как были.
- `ae2_emi_quick_fill.mixins.json`: `compatibilityLevel` `JAVA_21` → **`JAVA_25`** (классы
  компилируются под release 25 = class file 69; mixin 0.17.4+mixin.0.8.7, идущий с
  NeoForge 26.1.2.126, содержит `JAVA_25` — проверено javap по sponge-mixin из кэша).

### Код

- `Ae2AbstractRecipeHandlerMixin`: единственная содержательная правка — **цели миксина переехали**:
  - `appeng.integration.modules.emi.AbstractRecipeHandler` →
    **`appeng.client.integrations.emi.AbstractRecipeHandler`**;
  - `appeng.integration.modules.emi.EmiEncodePatternHandler` →
    **`appeng.client.integrations.emi.EmiEncodePatternHandler`**.
  Точка инъекции не изменилась: в обоих классах `canCraft` по-прежнему ровно один вызов
  `context.getType()` за guard'ом `== EmiCraftContext.Type.FILL_BUTTON`
  (сверяно с sources-jar AE2 26.1.12-SNAPSHOT).
  Крафт-терминал и беспроводной используют `EmiUseCraftingRecipeHandler`, который не переопределяет
  `canCraft` и покрывается первой целью; кодировщик образцов переопределяет — вторая цель.
  Refmap не нужен, как и раньше: в `@At`-дескрипторе только классы EMI (`dev.emi.*`), имена которых
  совпадают в рантайме, цель — классы AE2 (немапированные модовые имена).
- `EmiAe2ShiftCraft`: `@Mod#dist` в FML 11 стал `Dist[]` — записан явным одноэлементным массивом
  `{Dist.CLIENT}` (семантика прежняя, FMLModContainer в 11.0.15 фильтрует по
  `FMLLoader.getDist()`). Конструктор с `IEventBus` поддерживается — в байткоде FMLModContainer
  11.0.15 `IEventBus` присутствует в списке допустимых аргументов конструктора мода.

## Что проверено по реальным jar

- EMI `1.1.24-semi151a6c88+26.1.2` (mavenLocal, тот же jar лежит в
  `EMI-Compendium/qa-artifacts/server/mods/`): `EmiCraftContext#getType()` и enum `Type`
  (`FILL_BUTTON`, `CRAFTABLE`) на месте — javap.
- AE2 `26.1.12-SNAPSHOT` (mavenLocal): классы `appeng.client.integrations.emi.*` существуют,
  `canCraft` обоих целей содержит ровно один `getType()`; modid AE2 остался `ae2`; EMI-плагин
  регистрирует `EmiEncodePatternHandler` (pattern encoding), `EmiUseCraftingRecipeHandler`
  (crafting + wireless) — javap + sources-jar.
- FML loader 11.0.15 (из neoforge 26.1.2.126): `@Mod` = `{value, Dist[] dist, String[] depends}`,
  dist-фильтрация и `IEventBus`-конструктор на месте — javap.

Важный контекст: README ветки 1.21.1 утверждал, что 26.1.2 порт упирается в отсутствие EMI-интеграции
в AE2 26.1.12-beta — для 26.1.12-SNAPSHOT это уже не так, интеграция вернулась в новом пакете
`appeng.client.integrations.emi`. README на этой ветке поправлен.

## Сборка

```sh
JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 ./gradlew compileJava  # или clean build
```

Артефакт: `build/libs/ae2_emi_quick_fill-26.1.2-1.0.1+neoforge.26.1.2-port.1.jar`.

## Что осталось

- Runtime-QA: запустить клиент с реальными AE2 26.1.12 + EMI 1.1.24-semi151a6c88+26.1.2 и
  прогнать Shift-клик в терминалах (компиляция гарантирует только сходимость API).
- Проверить применение миксина в логе клиента (`defaultRequire = 1` уронит клиент при промахе —
  целевые методы сверялись, но рантайм-пруфа пока нет).
- `EMI-Compendium` рядом — косвенный рантайм-свидетель, что fleet-EMI жива на 26.1.2, но
  сам патч каналы AE2→EMI не трогает; поведение при отсутствии JEI-подобных сценариев не менялось.
