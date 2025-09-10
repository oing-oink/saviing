import React from 'react';

const ColorTestPage = () => {
  return (
    <div className="space-y-4 p-4">
      <h1 className="text-2xl font-bold text-primary">Home Page</h1>

      {/* 기본 색상 사용 예시 */}
      <div className="space-y-2 rounded-lg border p-4">
        <h2 className="text-lg font-semibold text-accent-foreground">
          Default Colors
        </h2>
        <button className="w-full rounded-lg bg-primary p-2 text-primary-foreground">
          Primary Button
        </button>
        <button className="w-full rounded-lg bg-secondary p-2 text-secondary-foreground">
          Secondary Button
        </button>
        <div className="rounded-lg bg-muted p-4 text-muted-foreground">
          This is a muted box.
        </div>
      </div>

      {/* 'game' 테마 적용 예시 */}
      <div className="game space-y-2 rounded-lg border p-4">
        <h2 className="text-lg font-semibold text-primary">
          Game Theme Section
        </h2>
        <p className="text-foreground">
          이 섹션 안의 요소들은 'game' 테마 색상을 따릅니다.
        </p>
        <button className="w-full rounded-lg bg-primary p-2 text-primary-foreground">
          Game Primary Button
        </button>
        <div className="flex h-10 w-full items-center justify-center rounded bg-sky-bg text-white">
          Sky Background
        </div>

        {/* 레벨 라벨 예시 */}
        <div className="mt-4 grid grid-cols-5 gap-2">
          {[...Array(10)].map((_, i) => (
            <div
              key={i}
              className={`flex items-center justify-center rounded-md p-2 font-bold text-white level-label-${String(i + 1).padStart(2, '0')}`}
            >
              Lv.{i + 1}
            </div>
          ))}
        </div>

        {/* 프로그레스바 예시 */}
        <div className="mt-4 space-y-4">
          <h3 className="text-lg font-semibold text-primary">Progress Bars</h3>

          {/* Energy Progress Bar */}
          <div>
            <p className="mb-1 text-foreground">포만감</p>
            <div className="progress-underlayer relative h-6 w-full overflow-hidden rounded-full">
              <div
                className="progress-energy h-full rounded-full"
                style={{ width: '75%' }}
              ></div>
            </div>
          </div>

          {/* Affection Progress Bar */}
          <div>
            <p className="mb-1 text-foreground">애정도</p>
            <div className="progress-underlayer relative h-6 w-full overflow-hidden rounded-full">
              <div
                className="progress-affection h-full rounded-full"
                style={{ width: '50%' }}
              ></div>
            </div>
          </div>

          {/* Experience Progress Bar */}
          <div>
            <p className="mb-1 text-foreground">경험치</p>
            <div className="progress-underlayer relative h-6 w-full overflow-hidden rounded-full">
              <div
                className="progress-experience h-full rounded-full"
                style={{ width: '90%' }}
              ></div>
            </div>
          </div>
        </div>
      </div>

      {/* 'saving' 테마 적용 예시 */}
      <div className="saving space-y-2 rounded-lg border p-4">
        <h2 className="text-lg font-semibold text-primary">
          Saving Theme Section
        </h2>
        <p className="text-foreground">
          이 섹션 안의 요소들은 'saving' 테마 색상을 따릅니다.
        </p>
        <button className="w-full rounded-lg bg-primary p-2 text-white">
          Saving Primary Button
        </button>
        <div className="flex h-10 w-full items-center justify-center rounded bg-saving-bg">
          Saving Background
        </div>
      </div>
    </div>
  );
};

export default ColorTestPage;
