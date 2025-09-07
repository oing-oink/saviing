import React from "react";

export default function AppLayout({ children }: { children: React.ReactNode }) {
  return (
    <div
      className="
        flex justify-center items-start   
        min-h-screen                      
        bg-gray-200                      
      "
    >
      <div
        className="
          w-full max-w-md         
          min-h-screen
          mx-auto
          safeArea
          bg-white                      
          shadow-md                      
          flex flex-col
        "
      >
        {children}
      </div>
    </div>
  );
}
