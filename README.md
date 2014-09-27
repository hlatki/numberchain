# Number Chain

## Usage (Taken from the perfection repository [here](https://github.com/astashov/perfection))

```bash
$ lein figwheel dev
$ lein cljsbuild auto test
$ lein cljsbuild auto release
$ lein repl
```

Then run in Vim `:Piggieback 9000` (make sure you have [vim-fireplace](https://github.com/tpope/vim-fireplace) installed), and open `htto://localhost:3449/index.html` in the browser. You are good to go!

## Release
To deploy the project to the server (assuming lein cljsbuild auto release already built everything and that you're at the root of the project), run
```bash
$ make release
```
to clean up the temp files, run
```bash
$ make clean
```
