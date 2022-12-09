import {Footer} from "flowbite-react"

function BottomFooter() {
    return (
        <Footer container={true}>
            <Footer.Copyright
                href="/"
                by="Conservation Colorado™"
                year={2023}
            />
            <Footer.LinkGroup>
                <Footer.Link href="https://github.com/OliverAbdulrahim/asana-hire/blob/main/LICENSE">
                    License
                </Footer.Link>
                <Footer.Link href="https://github.com/OliverAbdulrahim/asana-hire">
                    Source
                </Footer.Link>
            </Footer.LinkGroup>
        </Footer>
    );
}

export default BottomFooter
