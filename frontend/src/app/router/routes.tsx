import { createBrowserRouter } from "react-router-dom";
import HomePage from "@/pages/HomePage";
import LoginPage from "@/pages/LoginPage";
import SavingsPage from "@/pages/SavingsPage";
import GamePage from "@/pages/GamePage";
import NotFoundPage from "@/pages/NotFoundPage";

export const router = createBrowserRouter([
  { path: "/", element: <HomePage /> },
  { path: "/login", element: <LoginPage /> },
  { path: "/savings", element: <SavingsPage /> },
  { path: "/game", element: <GamePage /> },
  { path: "*", element: <NotFoundPage /> },
]);
