import js from "@eslint/js";
import globals from "globals";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import tseslint from "typescript-eslint";
import { globalIgnores } from "eslint/config";

export default tseslint.config([
  globalIgnores(["dist", "node_modules"]),
  {
    files: ["**/*.{ts,tsx}"],
    extends: [
      js.configs.recommended,
      tseslint.configs.recommended,
      reactHooks.configs["recommended-latest"],
      reactRefresh.configs.vite,
    ],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
    },
    rules: {
      "@typescript-eslint/no-unused-vars": "off",
      "no-implicit-coercion": "error", // 타입 변환은 명시적으로
      curly: ["error", "all"], // 모든 제어문에 중괄호 강제
      "no-warning-comments": [
        "warn",
        { terms: ["todo", "fixme", "bug"], location: "start" },
      ], // TODO/BUG는 경고로 추적
      "react-hooks/exhaustive-deps": "warn", // Hooks 의존성 검사
      "react-refresh/only-export-components": "warn", // Fast Refresh 최적화
    },
  },
]);
