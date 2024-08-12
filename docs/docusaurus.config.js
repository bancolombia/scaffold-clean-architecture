// @ts-check
// `@type` JSDoc annotations allow editor autocompletion and type checking
// (when paired with `@ts-check`).
// There are various equivalent ways to declare your Docusaurus config.
// See: https://docusaurus.io/docs/api/docusaurus-config

import {themes as prismThemes} from 'prism-react-renderer';

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Scaffold Clean Architecture',
  tagline: 'Gradle plugin to create a java application based on Clean Architecture following our best practices!',
  favicon: 'img/logo.svg',

  // Set the production url of your site here
  url: 'https://bancolombia.github.io',
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  baseUrl: '/scaffold-clean-architecture/',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'bancolombia', // Usually your GitHub org/user name.
  projectName: 'scaffold-clean-architecture', // Usually your repo name.

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  // Even if you don't use internationalization, you can use this field to set
  // useful metadata like html lang. For example, if your site is Chinese, you
  // may want to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: './sidebars.js',
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl:
          'https://github.com/bancolombia/scaffold-clean-architecture/tree/master/docs',
        },
        theme: {
          customCss: './src/css/custom.css',
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      // Replace with your project's social card
      image: 'https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png',
      navbar: {
        title: 'Scaffold Clean Architecture',
        logo: {
          alt: 'Scaffold Clean Architecture',
          src: 'img/logo.svg',
        },
        items: [
          {
            type: 'docSidebar',
            sidebarId: 'tutorialSidebar',
            position: 'left',
            label: 'Docs',
          },
          {
            href: 'https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a',
            label: 'Blog',
            position: 'right',
          },
          {
            href: 'https://github.com/bancolombia/scaffold-clean-architecture',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Docs',
            items: [
              {
                label: 'Overview',
                to: '/docs/intro',
              },
              {
                label: 'Scaffold Clean Architecture',
                to: '/docs/getting-started',
              },
            ],
          },
          {
            title: 'Community',
            items: [
              {
                label: 'Changelog',
                href: 'https://github.com/bancolombia/scaffold-clean-architecture/blob/master/CHANGELOG.md',
              },
              {
                label: 'Contributing',
                href: 'https://github.com/bancolombia/scaffold-clean-architecture/wiki/Contributing',
              },
              {
                label: 'License',
                href: 'https://github.com/bancolombia/scaffold-clean-architecture?tab=Apache-2.0-1-ov-file#readme',
              },
            ],
          },
          {
            title: 'More',
            items: [
              {
                label: 'Bancolombia Tech',
                href: 'https://medium.com/bancolombia-tech',
              },
              {
                label: 'GitHub',
                href: 'https://github.com/bancolombia/scaffold-clean-architecture',
              },
              {
                label: 'Gradle Plugin',
                href: 'https://plugins.gradle.org/plugin/co.com.bancolombia.cleanArchitecture',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} Grupo Bancolombia.`,
      },
      prism: {
        additionalLanguages: ['java', 'groovy', 'yaml'],
        theme: prismThemes.github,
        darkTheme: prismThemes.dracula,
      },
    }),
};

export default config;
