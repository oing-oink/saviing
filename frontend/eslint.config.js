import js from '@eslint/js';
import globals from 'globals';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import tseslint from 'typescript-eslint';
import noRelativeImportPaths from 'eslint-plugin-no-relative-import-paths';
import reactPlugin from 'eslint-plugin-react';
import importPlugin from 'eslint-plugin-import';
import unicorn from 'eslint-plugin-unicorn';
import filenames from 'eslint-plugin-filenames';
import { globalIgnores } from 'eslint/config';

export default tseslint.config([
  // 전역 ignore
  globalIgnores(['dist', 'node_modules', 'src/shared/components/ui/**/*']),

  {
    files: ['**/*.{ts,tsx}'],

    extends: [
      js.configs.recommended,
      tseslint.configs.recommended,
      reactHooks.configs['recommended-latest'],
      reactRefresh.configs.vite,
    ],

    plugins: {
      'no-relative-import-paths': noRelativeImportPaths,
      'react': reactPlugin,
      'import': importPlugin,
      unicorn,
      filenames,
    },

    languageOptions: {
      ecmaVersion: 'latest',
      globals: globals.browser,
      parserOptions: {
        ecmaFeatures: { jsx: true },
      },
    },

    settings: {
      react: { version: 'detect' },
    },

    rules: {
      // === TypeScript 컨벤션 ===
      '@typescript-eslint/consistent-type-imports': [
        'error',
        { prefer: 'type-imports' },
      ],
      '@typescript-eslint/array-type': ['error', { default: 'array' }], // string[] 선호
      '@typescript-eslint/no-explicit-any': 'warn',
      '@typescript-eslint/no-unused-vars': [
        'error',
        {
          argsIgnorePattern: '^_',
          varsIgnorePattern: '^_',
          ignoreRestSiblings: true,
        },
      ],
      '@typescript-eslint/consistent-type-definitions': ['error', 'interface'], // 객체는 interface 권장
      // React.FC 금지(문서 컨벤션 반영)
      '@typescript-eslint/no-restricted-types': [
        'error',
        {
          types: {
            'React.FC': '일반 함수 컴포넌트로 정의하세요',
            'React.FunctionComponent': '일반 함수 컴포넌트를 사용하세요',
          },
        },
      ],
      // 빈 함수 허용(문서: noop 허용)
      '@typescript-eslint/no-empty-function': 'off',

      // === React 컨벤션 ===
      'react/function-component-definition': [
        'error',
        {
          namedComponents: 'arrow-function',
          unnamedComponents: 'arrow-function',
        },
      ], // 컴포넌트는 화살표 함수
      'react-hooks/exhaustive-deps': 'warn',
      'react-refresh/only-export-components': 'warn',
      'react/jsx-pascal-case': ['error', { allowAllCaps: false }],

      // === Import/Export & 경로 ===
      'no-relative-import-paths/no-relative-import-paths': [
        'error',
        { allowSameFolder: true },
      ],

      // === 일반 품질 규칙 ===
      'prefer-const': 'error',
      'no-var': 'error',
      'curly': ['error', 'all'],
      'prefer-arrow-callback': 'error',
      'func-style': ['error', 'expression'], // 함수는 표현식(화살표 함수) 권장
      'no-implicit-coercion': 'error',

      // enum 금지(문서: as const 객체 사용)
      'no-restricted-syntax': [
        'error',
        {
          selector: 'TSEnumDeclaration',
          message: 'enum 대신 as const 객체를 사용하세요',
        },
      ],

      // TODO/FIXME 관리(문서)
      'no-warning-comments': [
        'warn',
        { terms: ['todo', 'fixme', 'bug'], location: 'start' },
      ],
    },
  },

  // === overrides ===
  {
    files: ['src/**/hooks/**/*.{ts,tsx}'],
    rules: { 'filenames/match-regex': ['error', '^use[A-Z].*'] },
  },

  { files: ['**/*.types.ts'], rules: {} },

  { files: ['src/vite-env.d.ts'], rules: {} },

  {
    files: ['src/shared/components/ui/**/*.{ts,tsx}'],
    rules: {
      'react-refresh/only-export-components': 'off',
    },
  },
]);
