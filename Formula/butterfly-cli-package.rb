class ButterflyCliPackage < Formula
  desc "Application code transformation tool"
  homepage "https://github.com/paypal/butterfly"
  url "https://search.maven.org/remotecontent?filepath=com/paypal/butterfly/butterfly-cli-package/2.2.0/butterfly-cli-package-2.2.0.zip"
  sha256 "7ae80466c5df8ef6d2c1a55bac3b584c94ce746eee1a4cd2674b458c2cae2d16"

  depends_on :java => "1.7+"

  def install
    inreplace "butterfly" do |s|
      s.prepend <<-EOS
        BUTTERFLY_HOME="#{prefix}"
      EOS
    end
    prefix.install("butterfly")
    prefix.install("lib")
    prefix.install("extensions")
    bin.install_symlink "#{prefix}/butterfly"
  end

  test do
    assert(system("butterfly", "-l"))
  end
end
