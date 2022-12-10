import {Footer} from "flowbite-react"

function BottomFooter() {
    return (
        <Footer container={true}>
            <Footer.Copyright
                href="https://github.com/OliverAbdulrahim/asana-hire/blob/main/LICENSE"
                by="asana-hire for Conservation Colorado"
            />
            <Footer.LinkGroup>
                <Footer.Link href="https://github.com/OliverAbdulrahim/asana-hire/blob/main/README.md">
                    About
                </Footer.Link>
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
