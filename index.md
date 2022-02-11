---
layout: default
title: Home
nav_order: 1
description: "ACMA smart agents are three agents built with Deep Q-learning method upon the old ACMA tool to refactor software systems"
permalink: /
---

# What is ACMA smart agents?
{: .fs-9 }

ACMA smart agents are three agents built with Deep Q-learning method upon tha old ACMA tool to refactor software systems. They contain two tools which must be run together: 1) custom ACMA tool and 2) its DQN Server.
{: .fs-6 .fw-300 }

[View ACMA on GitHub](https://github.com/hrahmadi71/a-cma){: .btn .bg-red-000 .text-grey-lt-000 .fs-5 .mb-4 .mb-md-0 .mr-2 } [View DQN Server on GitHub](https://github.com/hrahmadi71/acma_dqn_server){: .btn .fs-5 .mb-4 .mb-md-0 }

---

## Introduction

A-CMA refactors Java projects by receiving their Java byte-code as input. This tool first gets the Java byte-code and then extracts its structure (i.e. classes, methods, fields, relations, method-inputs, access levels, etc.).


|     Measure-ID      |     Description                                                                                                 |
|---------------------|-----------------------------------------------------------------------------------------------------------------|
| numFields           |     The number of fields   per class.                                                                           |
| avrgFieldVisibility |     The   average value of field visibility per   class (where private has the lowest and public has the highest values).|
| numConstants        |     The number of constant   fields per class. |
| numOps              |     The number of methods   per  class. |
| avrgMethodVisibility|     The average value of   method visibility per class. |
| setters             |     The number of set methods   per class. |
| getters             |     The number of   get-methods per class. |
| staticness          |     The number of static methods   per class. |
| nesting             |     The nesting level per   class. |
| abstractness        |     The ratio of abstract   classes to all classes in a package. |
| numCls              |     The number of classes per   package. |
| numInterf           |     The number of   interfaces in a package. |
| packageNesting      |     The nesting level per   package. |
| numOpsCls           |     The number of class operations   per package. |
| iFImpl              |     The number of   implemented interfaces by a class. |
| NOC                 |     The number of children   per class. |
| numDesc             |     The number of descendants   per class. |
| numAnc              |     The number of ancestors   per class. |
| iC_Attr             |     The number of classes   or interfaces used as attributes in a class. |
| eC_Attr             |     The number of external uses   of a class as an attribute in other classes. |
| iC_Par              |     The number of classes   or interfaces used as parameter types in class methods. |
| eC_Par              |     The number of external uses   of a class as parameter type in methods. |
| Dep_In              |     The number of elements   that depend on a class. |
| Dep_Out             |     The number of elements that   are depended on by a class. |
| NumAssEl_ssc        |     The number of associated   elements in the same namespace of a class. |
| NumAssEl_nsb        |     The number of   associated elements that are not in the same namespace of a class. |

### Dependencies

Just the Docs is built for [Jekyll](https://jekyllrb.com), a static site generator. View the [quick start guide](https://jekyllrb.com/docs/) for more information. Just the Docs requires no special plugins and can run on GitHub Pages' standard Jekyll compiler. The [Jekyll SEO Tag plugin](https://github.com/jekyll/jekyll-seo-tag) is included by default (no need to run any special installation) to inject SEO and open graph metadata on docs pages. For information on how to configure SEO and open graph metadata visit the [Jekyll SEO Tag usage guide](https://jekyll.github.io/jekyll-seo-tag/usage/).

### Quick start: Use as a GitHub Pages remote theme

1. Add Just the Docs to your Jekyll site's `_config.yml` as a [remote theme](https://blog.github.com/2017-11-29-use-any-theme-with-github-pages/)
```yaml
remote_theme: pmarsceill/just-the-docs
```
<small>You must have GitHub Pages enabled on your repo, one or more Markdown files, and a `_config.yml` file. [See an example repository](https://github.com/pmarsceill/jtd-remote)</small>

### Local installation: Use the gem-based theme

1. Install the Ruby Gem
```bash
$ gem install just-the-docs
```
```yaml
# .. or add it to your your Jekyll site’s Gemfile
gem "just-the-docs"
```
2. Add Just the Docs to your Jekyll site’s `_config.yml`
```yaml
theme: "just-the-docs"
```
3. _Optional:_ Initialize search data (creates `search-data.json`)
```bash
$ bundle exec just-the-docs rake search:init
```
3. Run you local Jekyll server
```bash
$ jekyll serve
```
```bash
# .. or if you're using a Gemfile (bundler)
$ bundle exec jekyll serve
```
4. Point your web browser to [http://localhost:4000](http://localhost:4000)

If you're hosting your site on GitHub Pages, [set up GitHub Pages and Jekyll locally](https://help.github.com/en/articles/setting-up-your-github-pages-site-locally-with-jekyll) so that you can more easily work in your development environment.

### Configure Just the Docs

- [See configuration options]({{ site.baseurl }})

---

## About the project

Just the Docs is &copy; 2017-{{ "now" | date: "%Y" }} by [Patrick Marsceill](http://patrickmarsceill.com).

### License

Just the Docs is distributed by an [MIT license](https://github.com/pmarsceill/just-the-docs/tree/master/LICENSE.txt).

### Contributing

When contributing to this repository, please first discuss the change you wish to make via issue,
email, or any other method with the owners of this repository before making a change. Read more about becoming a contributor in [our GitHub repo](https://github.com/pmarsceill/just-the-docs#contributing).

#### Thank you to the contributors of Just the Docs!

<ul class="list-style-none">
{% for contributor in site.github.contributors %}
  <li class="d-inline-block mr-1">
     <a href="{{ contributor.html_url }}"><img src="{{ contributor.avatar_url }}" width="32" height="32" alt="{{ contributor.login }}"/></a>
  </li>
{% endfor %}
</ul>

### Code of Conduct

Just the Docs is committed to fostering a welcoming community.

[View our Code of Conduct](https://github.com/pmarsceill/just-the-docs/tree/master/CODE_OF_CONDUCT.md) on our GitHub repository.
