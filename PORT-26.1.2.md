# Create Stock Bridge — fleet port to Minecraft 26.1.2

Fork of `tom5454/Create-Stock-Bridge` (MIT), `main` @ `d09c6fa` (0.2.0, MC 1.21.1). Fleet fork: `MehanikTMYT/Create-Stock-Bridge`.
Gradle project: `NeoForge/` (ModDevGradle 2.0.141, Gradle 9.2.1, Java 25, NeoForge 26.1.2.100).

## Dependencies (all local fleet builds)

| Dependency | Source |
|---|---|
| Create 6.0.11 for 26.1.2 | `worktrees/create-26.1.2` → `publishMavenJavaPublicationToMavenLocal` (`com.simibubi.create:create-26.1.2`) |
| Registrate / Ponder / Catnip / Flywheel | Create port's vendored `deps/maven` (`createPortMaven` in `gradle.properties`) |
| AE2 26.1.11-SNAPSHOT | `ae-stack/core/Applied-Energistics-2` → `publishToMavenLocal` |
| EMI 1.1.24-4524a4bf+26.1.2 | fleet EMI build in mavenLocal (dev runtime only) |

Create ships Registrate, Flywheel and Ponder inside its jar (jarJar); they are compile-only here and Create is
non-transitive, otherwise FML loads two copies.

## Recipe viewers

JEI and REI were removed (JEI deps, `ae2-jei-integration`, Jared's maven). The mod has no viewer-specific code;
EMI is the dev-runtime viewer.

## Code changes (1.21.1 → 26.1.2)

- Ponder/Catnip packages moved (`ponder.api.client.*`, `catnip.api.*`).
- `ResourceLocation` → `Identifier`, `GuiGraphics` → `GuiGraphicsExtractor`, `isClientSide()`, `FMLEnvironment.getDist()`.
- Networking: `playBidirectional(type, codec, server, client)`, `ClientPacketDistributor`.
- Datagen: `GatherDataEvent.Client`, `RecipeProvider.Runner`, Create's `CreateDataProviders` +
  `CreateBlockstateProvider`/`CreateItemModelProvider` vocabulary, default item components bound for the Ponder lang run.
- Persistence: AE2 grid node / crafting tracker / keys via `ValueOutput`/`ValueInput`, bridged into Create's
  `CompoundTag` block-entity API with Create's `ValueIo`; inventories as typed item lists.
- Item capability: `Capabilities.Item.BLOCK` through `CreateCapabilities.items(...)` so Create's packager still gets the
  `BridgeInventory` instance back (the mixin relies on that).
- AE2 26.1 API: typed `ClientActionKey`, `AEKeyRenderer` render-state split, `appeng.client.InitScreens`.
- Block: removal handled by the block entity (as in Create's `PackagerLinkBlock`); `neighborChanged(..., Orientation, ...)`.

## Verified

`compileJava`, `runClientData` (adds `assets/createstockbridge/items/*.json`), dedicated server: world created,
`setblock createstockbridge:ae_bridge`, block entity data round-trips, `save-all`, clean stop; the packager mixin applies.
Not yet verified in game: the AE ↔ Create stock flow itself (needs a client session).
