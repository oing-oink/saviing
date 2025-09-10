import Inventory from '@/features/game/shop/components/inventory';

const ShopPage = () => {
  return (
    <div className="font-galmuri">
      <div>Shop Page</div>
      <Inventory
        items={[
          {
            id: 1,
            name: 'sample',
            image: 'mouse_item.png',
            category: '냥이',
          },
        ]}
      />
    </div>
  );
};

export default ShopPage;
