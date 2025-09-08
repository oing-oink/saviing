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
  globalIgnores(['dist', 'node_modules']),
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
      react: reactPlugin,
      import: importPlugin,
      unicorn,
      filenames,
    },

    languageOptions: {
      ecmaVersion: 2022,
      globals: globals.browser,
      parserOptions: { ecmaFeatures: { jsx: true } },
    },

    settings: {
      react: { version: 'detect' },
    },

    rules: {
      '@typescript-eslint/no-explicit-any': 'warn', // any 사용시 경고
      '@typescript-eslint/consistent-type-imports': [
        'error',
        { prefer: 'type-imports' },
      ],
      '@typescript-eslint/array-type': ['error', { default: 'array' }],
      '@typescript-eslint/ban-types': [
        'error',
        {
          types: {
            React.FC: '일반 함수 컴포넌트로 정의하세요',
            React.FunctionComponent: '일반 함수 컴포넌트를 사용하세요',
          },
        },
      ],
      '@typescript-eslint/no-unused-vars': [
        'error',
        {
          argsIgnorePattern: '^_',
          varsIgnorePattern: '^_',
          ignoreRestSiblings: true,
        },
      ],
      '@typescript-eslint/naming-convention': [
        'error',
        { selector: 'typeLike', format: ['PascalCase'] },
        {
          selector: 'variable',
          format: ['camelCase', 'UPPER_CASE', 'PascalCase'],
          leadingUnderscore: 'allow',
        },
        {
          selector: 'variable',
          modifiers: ['const'],
          format: ['UPPER_CASE'],
          filter: { regex: '^[A-Z0-9_]+$', match: false },
        },
        {
          selector: 'variable',
          types: ['boolean'],
          format: ['camelCase'],
          prefix: ['is', 'has', 'can'],
        },
        { selector: 'function', format: ['camelCase'] },
      ],

      'react-hooks/exhaustive-deps': 'warn',
      'react-refresh/only-export-components': 'warn',
      'react/jsx-pascal-case': ['error', { allowAllCaps: false, ignore: [] }],

      'no-relative-import-paths/no-relative-import-paths': [
        'error',
        { allowSameFolder: true }, // 같은 폴더는 허용
      ],

      'import/no-default-export': 'error',
      'prefer-const': 'error',
      'no-var': 'error',
      curly: ['error', 'all'],
      'prefer-arrow-callback': 'error',
      'func-style': ['error', 'expression'],
      'no-implicit-coercion': 'error',
      'no-warning-comments': [
        'warn',
        { terms: ['todo', 'fixme', 'bug'], location: 'start' },
      ],

      // 실무 편의상 off
      'no-magic-numbers': 'off',

      'no-restricted-syntax': [
        'error',
        {
          selector: 'TSEnumDeclaration',
          message: 'enum 대신 as const 객체를 사용하세요',
        },
      ],

      'unicorn/filename-case': [
        'error',
        {
          cases: {
            camelCase: true,
            pascalCase: true,
          },
        },
      ],
    },

    overrides: [
      {
        files: ['src/**/components/**/*.{tsx,ts}'],
        rules: {
          'import/no-default-export': 'off',
          'unicorn/filename-case': [
            'error',
            { cases: { pascalCase: true } },
          ],
        },
      },
      {
        files: ['src/**/hooks/**/*.{ts,tsx}'],
        rules: {
          'filenames/match-regex': ['error', '^use[A-Z].*'],
        },
      },
      {
        files: ['**/*.types.ts'],
        rules: {
          'unicorn/filename-case': [
            'error',
            { cases: { camelCase: true } },
          ],
        },
      },
      {
        files: ['src/**/pages/**/*.{tsx,ts}'],
        rules: {
          'import/no-default-export': 'off', // pages 디렉토리도 허용
        },
      },
    ],
  },
]);
