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
          w-full max-w-[430px]          
          min-h-screen
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
