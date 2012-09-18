# lein-misaki

A [Leiningen](https://github.com/technomancy/leiningen) plugin for [Misaki](https://github.com/liquidz/misaki), a [Jekyll](https://github.com/mojombo/jekyll) inspired static site generator

## Usage

Create a `mysite` folder and put a `_config.clj` file in it. The
minimal configuration is an empty clojure map, i.e. `{}`

Read the
Misaki
[default structure](https://github.com/liquidz/misaki/wiki/Directory-Structure)
to learn how to lay down your site sources, or peek at the 
[sample](https://github.com/liquidz/misaki/blob/master/samples/blog/_config.clj)
to see all the available options.

Put `[lein-misaki "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your
`:user` profile, or if you are on Leiningen 1.x do `lein plugin install
lein-misaki 0.1.0-SNAPSHOT`.

You can launch with

    $ lein misaki

to compile the static site resources and serve it with an embedded
Jetty, or

    $ lein misaki --compile

to only compile the static site.

## License

Copyright © 2012 Carlo Sciolla

Distributed under the Eclipse Public License, the same as Clojure.
