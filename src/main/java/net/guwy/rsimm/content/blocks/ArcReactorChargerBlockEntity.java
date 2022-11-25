package net.guwy.rsimm.content.blocks;

import net.guwy.rsimm.index.ModBlockEntities;
import net.guwy.rsimm.index.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArcReactorChargerBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(3){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200;

    public ArcReactorChargerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ARC_REACTOR_CHARGER.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> ArcReactorChargerBlockEntity.this.progress;
                    case 1 -> ArcReactorChargerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pValue) {
                    case 0 -> ArcReactorChargerBlockEntity.this.progress = pValue;
                    case 1 -> ArcReactorChargerBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.rsimm.arc_reactor_charger");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ArcReactorChargerMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER){
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("progress", this.progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("progress");
    }

    public void drops(){
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i=0; i < itemHandler.getSlots(); i++){
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static final DirectionProperty FACING = ArcReactorChargerBlock.FACING;
    public static final BooleanProperty ACTIVE = ArcReactorChargerBlock.ACTIVE;

    public static  void tick(Level level, BlockPos blockPos, BlockState state, ArcReactorChargerBlockEntity pEntity) {
        if(level.isClientSide()){
            return;
        }

        if(hasRecipe(pEntity)){
            pEntity.progress++;
            setChanged(level, blockPos, state);

            if(!pEntity.getBlockState().getValue(ArcReactorChargerBlock.ACTIVE)){
                level.setBlock(blockPos, state
                                .setValue(FACING, state.getValue(FACING))
                                .setValue(ACTIVE, true)
                        , 2);
            }

            if(pEntity.progress >= pEntity.maxProgress){
                craftItem(pEntity);
            }
        }   else {
            pEntity.resetProgress();
            setChanged(level,blockPos,state);
            state.setValue(ArcReactorChargerBlock.ACTIVE, false);

            if(pEntity.getBlockState().getValue(ArcReactorChargerBlock.ACTIVE)){
                level.setBlock(blockPos, state
                                .setValue(FACING, state.getValue(FACING))
                                .setValue(ACTIVE, false)
                        , 2);
            }
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(ArcReactorChargerBlockEntity pEntity) {
        if(hasRecipe(pEntity)){
            pEntity.itemHandler.extractItem(0, 1, false);

            ItemStack result = new ItemStack(ModItems.MARK_1_ARC_REACTOR.get(), pEntity.itemHandler.getStackInSlot(1).getCount() + 1);
            CompoundTag nbtTag = new CompoundTag();
            nbtTag.putLong("energy", 100);
            result.setTag(nbtTag);
            pEntity.itemHandler.setStackInSlot(0, result);

            pEntity.resetProgress();
        }
    }

    private static boolean hasRecipe(ArcReactorChargerBlockEntity pEntity) {
        SimpleContainer inventory = new SimpleContainer(pEntity.itemHandler.getSlots());
        for(int i=0; i < pEntity.itemHandler.getSlots(); i++){
            inventory.setItem(i, pEntity.itemHandler.getStackInSlot(i));
        }

        boolean hasCorrectItemInSlot = pEntity.itemHandler.getStackInSlot(0).getItem() == Items.AMETHYST_SHARD;

        return hasCorrectItemInSlot && inventory.getItem(0).getCount() == 1;
    }
}