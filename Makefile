OUTDIR=resources/public

release:
	mkdir $(OUTDIR)/tmp
	cp -R $(OUTDIR)/css $(OUTDIR)/tmp
	cp $(OUTDIR)/*.js $(OUTDIR)/tmp/
	cp $(OUTDIR)/index_prod.html  $(OUTDIR)/tmp/index.html
	scp -r $(OUTDIR)/tmp/* release@numberchain.clojurecup.com:/var/www

clean:
	rm -rf $(OUTDIR)/tmp
