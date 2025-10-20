# JEI Migration to Minecraft 1.21.2

This document tracks the migration of Just Enough Items (JEI) from Minecraft 1.21.1 to 1.21.2 for the CouleeTechMinecraftMods fork.

## Migration Status

**Branch:** `mc-1.21.2-migration`
**Status:** 🟡 In Progress - API Incompatibilities
**Progress:** Dependencies resolved, code migration needed

## Completed Steps

### ✅ Version Updates
- Updated `minecraftVersion` to 1.21.2
- Updated `neoforgeVersion` to 21.2.1-beta
- Updated `neoformTimestamp` to 20241022.151510
- Updated version ranges for 1.21.2 compatibility

### ✅ Build Configuration
- Disabled Forge subprojects (Forge no longer supports 1.21.2+)
- Added Minecraft Libraries repository for dependency resolution
- Fixed jtracy dependency issue in NeoForge ModDevGradle
- Added repositories to all subprojects

## Remaining Work

### 🔧 API Incompatibility Fixes Needed

The build currently fails with 4 compilation errors due to Minecraft API changes between 1.21.1 and 1.21.2:

#### 1. RecipeHolder.id() Return Type Change
**Location:** `CommonApi/src/main/java/mezz/jei/api/recipe/category/IRecipeCategory.java:271`

**Error:**
```
incompatible types: ResourceKey<Recipe<?>> cannot be converted to ResourceLocation
return recipeHolder.id();
```

**Fix Required:** `RecipeHolder.id()` now returns `ResourceKey<Recipe<?>>` instead of `ResourceLocation`. Need to convert using `id().location()`.

#### 2. RecipeHolder.id() in Lambda Expression
**Location:** `CommonApi/src/main/java/mezz/jei/api/recipe/category/extensions/vanilla/crafting/ICraftingCategoryExtension.java:87`

**Error:**
```
incompatible types: bad return type in lambda expression
.or(() -> Optional.of(recipeHolder.id()));
```

**Fix Required:** Same as above, use `id().location()`.

#### 3. Ingredient.getItems() Method Removed (x2 occurrences)
**Locations:**
- `CommonApi/src/main/java/mezz/jei/api/gui/builder/IIngredientConsumer.java:59`
- `CommonApi/src/main/java/mezz/jei/api/gui/builder/IIngredientAcceptor.java:57`

**Error:**
```
cannot find symbol: method getItems()
addIngredients(VanillaTypes.ITEM_STACK, List.of(ingredient.getItems()));
```

**Fix Required:** The `Ingredient.getItems()` method has been replaced. Check Minecraft 1.21.2 documentation for the new API. Likely need to use `ingredient.items()` or similar.

## How to Complete Migration

1. **Fix RecipeHolder API changes:**
   - Replace `recipeHolder.id()` with `recipeHolder.id().location()` where ResourceLocation is expected
   - Update all usages in IRecipeCategory and ICraftingCategoryExtension

2. **Fix Ingredient API changes:**
   - Research the new Ingredient API in Minecraft 1.21.2
   - Replace `ingredient.getItems()` with the appropriate new method
   - Update IIngredientConsumer and IIngredientAcceptor

3. **Test the build:**
   ```bash
   ./gradlew :NeoForge:build
   ```

4. **Publish to mavenLocal:**
   ```bash
   ./gradlew :NeoForge:publishToMavenLocal
   ```

## Integration with Create

Once JEI is successfully building:

1. The artifact will be published as: `mezz.jei:jei-1.21.2-neoforge:19.21.2.9999`
2. Update Create's `build.gradle` to uncomment JEI dependency:
   ```gradle
   implementation("mezz.jei:jei-$minecraft_version-neoforge:$jei_version")
   ```
3. Update Create's `gradle.properties`:
   ```properties
   jei_version = 19.21.2.9999
   ```

## Resources

- **NeoForge Migration Docs:** `/home/tholum-mc/CreateMod/NeoForgeDocumentation/primer/docs/1.21.2/index.md`
- **Minecraft 1.21.2 Changes:** Check NeoForge changelog for API changes
- **Upstream JEI:** https://github.com/mezz/JustEnoughItems
- **Our Fork:** https://github.com/CouleeTechMinecraftMods/JustEnoughItems/tree/mc-1.21.2-migration

## Notes

- JEI is a multi-platform mod (NeoForge + Fabric)
- This migration only covers NeoForge platform
- Forge platform is disabled (no longer supports 1.21.2+)
- The errors are minimal and focused on recipe/ingredient API changes

---

*Last Updated: 2025-10-19*
*Migrated By: Claude Code*
